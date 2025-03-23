import { Component, ElementRef, inject, OnInit, ViewChildren, QueryList } from '@angular/core';
import { MealService } from '../../../services/meal.service';
import { WorkoutService } from '../../../services/workout.service';
import { ActivatedRoute } from '@angular/router';
import { BarController, BarElement, CategoryScale, Chart, LinearScale, Title } from 'chart.js';
import { TotalMealData } from '../../../model/meal.model';
import { TotalWorkoutData } from '../../../model/workout.model';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AchievementService } from '../../../services/achievement.service';
import { Achievement } from '../../../model/model';

// Register the components with Chart.js
Chart.register(BarElement, CategoryScale, LinearScale, BarController, Title);
@Component({
  selector: 'app-data',
  standalone: false,
  templateUrl: './data.component.html',
  styleUrls: ['./data.component.css']
})
export class DataComponent implements OnInit {

  private activeRoute = inject(ActivatedRoute);
  private workoutSvc = inject(WorkoutService);
  private mealSvc = inject(MealService);
  protected user: string = ''
  protected achievementSvc = inject(AchievementService)

  protected subscription: string = ''

  @ViewChildren('MyChart') chartCanvases: QueryList<ElementRef> | undefined;
  workoutChart: Chart | undefined;
  mealChart: Chart | undefined;

  chartWorkout: TotalWorkoutData[] = [];
  chartMeals: TotalMealData[] = [];

  filteredWorkoutData: TotalWorkoutData[] = [];
  filteredMealData: TotalMealData[] = [];

  userAchievements: Achievement[] = [];

  // Create a FormGroup for filters
  filterForm = new FormGroup({
    month: new FormControl(''),
    year: new FormControl('')
  });

  // Available options
  availableYears: string[] = [];
  availableMonths = [
    { value: '01', label: 'January' },
    { value: '02', label: 'February' },
    { value: '03', label: 'March' },
    { value: '04', label: 'April' },
    { value: '05', label: 'May' },
    { value: '06', label: 'June' },
    { value: '07', label: 'July' },
    { value: '08', label: 'August' },
    { value: '09', label: 'September' },
    { value: '10', label: 'October' },
    { value: '11', label: 'November' },
    { value: '12', label: 'December' }
  ];

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      const name: string = params['name'];
      this.subscription = localStorage.getItem("subscription") || "free";
      this.user = name;
      this.loadData(name);
    });


    this.filterForm.valueChanges.subscribe(() => {
      this.filterByMonthAndYear();
    });
    this.loadAchievements()
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.initializeCharts(), 0);
  }
  loadAchievements(): void {

    this.achievementSvc.getAchievements(this.user)
      .then(achievements => {
        
        this.userAchievements = achievements;
        console.log('Loaded achievements:', achievements);
      })
      .catch(error => {
        console.error('Error loading achievements:', error);
      });
  }


  getAchievementName(code: string): string {
    return this.achievementSvc.getAchievementDisplayName(code);
  }

  getAchievementIcon(code: string): string {
    return this.achievementSvc.getAchievementIcon(code);
  }

  loadData(name: string): void {
    this.workoutSvc.getWorkoutChartData(name).then(workout => {
      this.chartWorkout = workout;
      this.filteredWorkoutData = [...workout];
      this.extractAvailableYears(workout);
      return this.mealSvc.getMealChartData(name);
    }).then(meal => {
      this.chartMeals = meal;
      this.filteredMealData = [...meal];
      this.extractAvailableYears(meal);
      this.initializeCharts();
    }).catch(error => {
      console.error('Error fetching data:', error);
    });
  }

  private extractAvailableYears(data: any[]): void {
    const years = new Set<string>();

    data.forEach(item => {
      if (item.date && typeof item.date === 'string') {
        const year = item.date.split('-')[0];
        years.add(year);
      }
    });

    const combinedYears = Array.from(
      new Set([...this.availableYears, ...Array.from(years)])
    ).sort();

    this.availableYears = combinedYears;
  }

  filterByMonthAndYear(): void {
    const formValues = this.filterForm.value;
    const selectedMonth = formValues.month || '';
    const selectedYear = formValues.year || '';

    this.filteredWorkoutData = [...this.chartWorkout];
    this.filteredMealData = [...this.chartMeals];

    if (selectedMonth) {
      this.filteredWorkoutData = this.filteredWorkoutData.filter(item =>
        item.date.split('-')[1] === selectedMonth
      );

      this.filteredMealData = this.filteredMealData.filter(item =>
        item.date.split('-')[1] === selectedMonth
      );
    }

    if (selectedYear) {
      this.filteredWorkoutData = this.filteredWorkoutData.filter(item =>
        item.date.split('-')[0] === selectedYear
      );

      this.filteredMealData = this.filteredMealData.filter(item =>
        item.date.split('-')[0] === selectedYear
      );
    }

    this.updateCharts();
  }

  clearFilters(): void {
    this.filterForm.reset({
      month: '',
      year: ''
    });
  }

  private initializeCharts(): void {
    if (!this.chartCanvases || this.chartCanvases.length < 2) return;

    const canvasElements = this.chartCanvases.toArray();

    if (!this.workoutChart && canvasElements[0]) {
      this.workoutChart = this.createChart(
        canvasElements[0].nativeElement,
        this.getWorkoutChartData()
      );
    }

    if (!this.mealChart && canvasElements[1]) {
      this.mealChart = this.createChart(
        canvasElements[1].nativeElement,
        this.getMealChartData()
      );
    }
  }

  private updateCharts(): void {
    if (this.workoutChart) {
      this.workoutChart.data = this.getWorkoutChartData();
      this.workoutChart.update();
    }

    if (this.mealChart) {
      this.mealChart.data = this.getMealChartData();
      this.mealChart.update();
    }
  }

  private getWorkoutChartData() {
    const labels = this.filteredWorkoutData.map(workout => workout.date);
    const caloriesBurnt = this.filteredWorkoutData.map(workout => {
      return workout.total_calories
    });


    return {
      labels: labels,
      datasets: [
        {
          label: 'Calories Burnt',
          data: caloriesBurnt,
          backgroundColor: 'red'
        }
      ]
    };
  }

  private getMealChartData() {
    const labels = this.filteredMealData.map(meal => meal.date);
    const caloriesConsumed = this.filteredMealData.map(meal => {
      return meal.total_calories
    });

    return {
      labels: labels,
      datasets: [
        {
          label: 'Calories Consumed',
          data: caloriesConsumed,
          backgroundColor: 'blue'
        }
      ]
    };
  }

  private createChart(canvas: HTMLCanvasElement, chartData: any): Chart {
    return new Chart(canvas, {
      type: 'bar',
      data: chartData,
      options: {
        aspectRatio: 3,
        scales: {
          x: {
            title: {
              display: true,
              text: 'Date',
              font: { size: 20 }
            }
          },
          y: {
            title: {
              display: true,
              text: 'Amount of Calories, kcal',
              font: { size: 20 }
            }
          }
        }
      }
    });
  }
}