import { Component, inject, OnInit, ViewChild, ElementRef, OnDestroy, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { UserDetails } from '../../../model/model';
import { map, Observable, Subscription, take } from 'rxjs';
import { MealsInfo, TotalMealData } from '../../../model/meal.model';
import { Store } from '@ngrx/store';
import { TotalWorkoutData, Workouts } from '../../../model/workout.model';
import { DexieService } from '../../../services/dexie.service';
import { WorkoutService } from '../../../services/workout.service';
import { MealService } from '../../../services/meal.service';
import { loadMeals } from '../../../ngRx/meal_info.action';
import { Chart, PieController, ArcElement, Legend, Title, Tooltip } from 'chart.js';

// Register required Chart.js components
Chart.register(PieController, ArcElement, Legend, Title, Tooltip);

@Component({
  selector: 'app-homepage',
  standalone: false,
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit, AfterViewInit, OnDestroy {

  private activeRoute = inject(ActivatedRoute);
  private userSvc = inject(UserService);
  private store = inject(Store);
  private dexieSvc = inject(DexieService);
  private mealSvc = inject(MealService);

  @ViewChild('NutrientChart') nutrientChartCanvas?: ElementRef;

  totalCalories$: Observable<number>;

  userInfo: UserDetails = {} as UserDetails;
  meals$!: Observable<MealsInfo[]>;

  workoutList: Workouts[] = [];
  date: string = new Date().toISOString().split('T')[0];

  chartWorkout: TotalWorkoutData[] = [];
  chartMeals: TotalMealData[] = [];

  nutrientChart: Chart | null = null;
  protected missed:number = 0;

  totalCalories: number = 0;
  private subscriptions: Subscription[] = [];
  protected subscription:string = ''

  constructor() {
    this.meals$ = this.store.select(state => state.mealsInfo.meals);
    this.totalCalories$ = this.meals$.pipe(
      map(meals => meals.reduce((total, meal) => total + meal.calories, 0))
    );
  }

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      const name: string = params['name'];

      this.userSvc.getInfo(name).then(result => {
        this.userInfo = result;
        this.subscription = result.subscription
        const mealsSub = this.meals$.pipe(take(1)).subscribe(meals => {
          if (meals.length === 0) {
            this.mealSvc.getTodayMeals(name).then(mealsData => {
              this.store.dispatch(loadMeals({ meals: mealsData }));
            })
            .catch(error => {
              console.error('Error loading meals:', error);
            });
          }
        });
        this.subscriptions.push(mealsSub);

        this.dexieSvc.getUser(this.userInfo.name).then(user => {
          this.workoutList = user?.workouts ?? [];

          const today = new Date();
          const todayString = today.toISOString().split('T')[0];

          const filteredWorkouts = this.workoutList.filter(workout => {
            return workout.date === todayString;
          });

          this.workoutList = filteredWorkouts;

          const filteredCalWorkouts = filteredWorkouts.filter(workout => {
            return workout.status === 'completed';
          });

          this.totalCalories = filteredCalWorkouts.reduce((total, workout) => {
            return total + (workout.calories ?? 0);
          }, 0);
        });
      });
    });
  }
  
  ngAfterViewInit(): void {
    setTimeout(() => {
      this.updateNutrientChart();
      const mealsSub = this.meals$.subscribe(() => {
        this.updateNutrientChart();
      });
      this.subscriptions.push(mealsSub);
    }, 0);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    
    if (this.nutrientChart) {
      this.nutrientChart.destroy();
    }
  }

  private updateNutrientChart(): void {
    if (!this.nutrientChartCanvas) {
      return;
    }
    
    this.meals$.pipe(take(1)).subscribe(meals => {
      const nutrients = this.calculateNutrients(meals);
      
      if (this.nutrientChart) {
        this.nutrientChart.destroy();
      }
      
      this.nutrientChart = new Chart(this.nutrientChartCanvas?.nativeElement, {
        type: 'pie',
        data: {
          labels: ['Fat (g)', 'Sugar (g)', 'Protein (g)'],
          datasets: [{
            data: [nutrients.totalFat, nutrients.totalSugar, nutrients.totalProtein],
            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
            hoverBackgroundColor: ['#FF4C75', '#2693DC', '#FFB52E']
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'top',
              labels: {
                font: {
                  size: 15
                }
              }
            },
            title: {
              display: true,
              text: 'Nutritional Breakdown',
              font: {
                size: 20
              }
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const label = context.label || '';
                  const value = context.parsed || 0;
                  const total = context.dataset.data.reduce((a: number, b: number) => a + b, 0);
                  const percentage = Math.round((value / total) * 100);
                  const roundedTotal = total.toFixed(2); 
                  const roundedValue = value.toFixed(2);
                  return `${label}: ${roundedValue}g (${percentage}%) (Total: ${roundedTotal}g)`;
                }
              }
            }
          }
        }
      });
    });
  }

  private calculateNutrients(meals: MealsInfo[]) {
    let totalFat = 0;
    let totalSugar = 0;
    let totalProtein = 0;

    meals.forEach(meal => {
      totalFat += meal.fat || 0;
      totalSugar += meal.sugars || 0;
      totalProtein += meal.protein || 0;
    });

    return { totalFat, totalSugar, totalProtein };
  }
}