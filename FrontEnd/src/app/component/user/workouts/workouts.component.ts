import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { WeatherService } from '../../../services/weather.service';
import { Weather } from '../../../model/model';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Completed, Workouts } from '../../../model/workout.model';
import { WorkoutService } from '../../../services/workout.service';
import { ActivatedRoute } from '@angular/router';
import { DexieService } from '../../../services/dexie.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-workouts',
  standalone: false,
  templateUrl: './workouts.component.html',
  styleUrl: './workouts.component.css'
})
export class WorkoutsComponent implements OnInit, OnDestroy {

  private fb = inject(FormBuilder);
  protected form!: FormGroup;
  private weatherSvc = inject(WeatherService);
  private workoutSvc = inject(WorkoutService);
  weatherList: Weather[] = [];
  date: string = new Date().toISOString().split('T')[0];
  private activeRoute = inject(ActivatedRoute);
  user: string = '';
  protected subs:string = ''
  workoutList: Workouts[] = [];

  private dexisSvc = inject(DexieService);

  private subscriptions: Subscription[] = [];

  private createForm(): FormGroup {
    return this.fb.group({
      details: this.fb.control<string>('', Validators.required),
      type: this.fb.control<string>('', Validators.required),
      duration: this.fb.control<number>(0, [Validators.required, Validators.min(1)]),
      status: this.fb.control<string>('', Validators.required),
      date: this.fb.control<string>(`${this.date}`, this.checkDate()),
    });
  }

  checkDate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const inputDate = new Date(control.value);
      const today = new Date();  
      today.setHours(0, 0, 0, 0);
      if (inputDate < today) {
        return { dateInvalid: true }; 
      }
      return null; 
    };
  }

  ngOnInit(): void {
    this.weatherSvc.getWeatherInfo().then(result => {
      if (Array.isArray(result)) {
        this.weatherList = result;
      } else {
        console.warn('getWeatherInfo() did not return an array:', result);
      }
    });

    this.form = this.createForm();
    this.subs = localStorage.getItem("subscription") || "free";
    

    const routeSub = this.activeRoute.params.subscribe(params => {
      this.user = params['name'];
      this.loadUserWorkouts();
    });
    this.subscriptions.push(routeSub);
  }
  
  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
  
  private loadUserWorkouts(): void {
    this.dexisSvc.getUser(this.user).then(user => {
      if (user) {
        this.workoutList = user.workouts;
      }
    });
  }

  save() {
    const value: Workouts = this.form.value;
    value.name = this.user;
    this.workoutSvc.getWorkoutInfo(value).then(result => {
      this.dexisSvc.getUser(this.user).then(user => {
        if (user) {
          user.workouts.push(result);
          this.dexisSvc.updateUser(user);
          this.workoutList = user.workouts;
          alert("Workout added");
          
          this.form.reset({
            date: this.date
          });
        }
      });
    });
  }

  getWeatherIconUrl(iconCode: string): string {
    return `https://www.weatherbit.io/static/img/icons/${iconCode}.png`;
  }

  changeStatus(workout: Workouts, i: number) {
    workout.status = "completed";

    const completed: Completed = {
      name: this.user,
      index: i
    };
    this.workoutSvc.completedPlan(completed);
    this.dexisSvc.getUser(this.user).then(user => {
      if (user) {
        const completedWorkouts = user.workouts.filter(workout => workout.status === 'completed');
        const plannedWorkouts = user.workouts.filter(workout => workout.status === 'planned');
        
        if (i >= 0 && i < plannedWorkouts.length) {
          const workout: Workouts = plannedWorkouts[i];
          workout.status = "completed";
          plannedWorkouts.splice(i, 1);
          completedWorkouts.push(workout);
          user.workouts = [...completedWorkouts, ...plannedWorkouts];
          this.dexisSvc.updateUser(user);
          
          this.workoutList = user.workouts;
        } else {
          console.warn('Invalid index for planned workouts:', i);
        }
      }
    });
  }

  get plannedWorkouts() {
    return this.workoutList.filter(item => item.status === 'planned');
  }
}