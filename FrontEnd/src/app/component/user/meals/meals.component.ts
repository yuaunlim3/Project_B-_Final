import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MealService } from '../../../services/meal.service';
import { Meals, MealsInfo } from '../../../model/meal.model';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { addMeal } from '../../../ngRx/meal_info.action';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-meals',
  standalone: false,
  templateUrl: './meals.component.html',
  styleUrl: './meals.component.css'
})
export class MealsComponent implements OnInit, OnDestroy {

  private fb = inject(FormBuilder);
  protected form!: FormGroup;
  private mealSvc = inject(MealService);
  private store = inject(Store);
  private activeRoute = inject(ActivatedRoute);
  user: string = '';
  previous_Search: MealsInfo[] = [];
  mealInfo: MealsInfo | null = null;

  advice: string = 'This is the advice for the user';

  meals$: Observable<MealsInfo[]>;
  protected subs: string = ''

  private subscriptions: Subscription[] = [];

  isAdviceLoading: boolean = false;


  constructor() {
    this.meals$ = this.store.select(state => state.mealInfo.meals);
  }

  date: string = new Date().toISOString().split('T')[0];

  ngOnInit(): void {
    this.form = this.createForm();
    this.subs = localStorage.getItem("subscription") || "free";

    const routeSub = this.activeRoute.params.subscribe(params => {
      this.user = params['name'];
    });
    this.subscriptions.push(routeSub);

    if (this.subs == "premium") {
      this.isAdviceLoading = true;
      this.mealSvc.getAdvice(this.user).then(result => {
        this.advice = result.advice;
        this.isAdviceLoading = false;
      }).catch(error => {
        console.error("Error fetching advice:", error);
        this.isAdviceLoading = false;
      });
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  search() {
    const value: Meals = this.form.value;
    this.mealSvc.getMealInfo(value).then(result => {
      this.mealInfo = result;
      this.previous_Search.push(this.mealInfo);
    });
  }

  add(mealInfo: MealsInfo) {
    this.store.dispatch(addMeal({ meal: mealInfo }));
    const newMeal = { ...mealInfo, name: this.user };
    this.mealSvc.saveMeal(newMeal);
    alert("Meal Added");
  }

  reset() {
    this.form.reset();
    this.mealInfo = null;
  }

  private createForm(): FormGroup {
    return this.fb.group({
      food: this.fb.control<string>('', Validators.required),
      amount: this.fb.control<number>(1, Validators.required),
      serving_size: this.fb.control<string>('serving', Validators.required),
      meal_type: this.fb.control<string>('', Validators.required),
    });
  }
}