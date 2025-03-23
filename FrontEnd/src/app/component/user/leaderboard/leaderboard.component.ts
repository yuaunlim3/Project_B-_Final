import { Component, OnInit } from '@angular/core';
import { LeaderboardService } from '../../../services/leaderboard.service';

interface LeaderboardEntry {
  name: string;
  score: number;
  rank: number;
}

@Component({
  selector: 'app-leaderboard',
  standalone: false,
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.css'
})
export class LeaderboardComponent implements OnInit {
  // Active category for leaderboard
  activeCategory: string = 'login_streak';

  // Leaderboard data for each category
  leaderboardData: {
    login_streak: LeaderboardEntry[];
    total_meals: LeaderboardEntry[];
    total_workouts: LeaderboardEntry[];
  } = {
    login_streak: [],
    total_meals: [],
    total_workouts: []
  };

  // Loading and error states for each category
  loadingStates: {
    login_streak: boolean;
    total_meals: boolean;
    total_workouts: boolean;
  } = {
    login_streak: false,
    total_meals: false,
    total_workouts: false
  };

  errors: {
    login_streak: string | null;
    total_meals: string | null;
    total_workouts: string | null;
  } = {
    login_streak: null,
    total_meals: null,
    total_workouts: null
  };

  constructor(private leaderboardService: LeaderboardService) { }

  ngOnInit(): void {
    this.loadLoginStreakLeaderboard();
  }

  loadLoginStreakLeaderboard(): void {
    this.loadingStates.login_streak = true;
    this.errors.login_streak = null;

    this.leaderboardService.getLoginStreakLeaderboard()
      .then(entries => {
        this.leaderboardData.login_streak = entries;
        this.loadingStates.login_streak = false;
      })
      .catch(error => {
        this.errors.login_streak = 'Failed to load login streak leaderboard.';
        this.loadingStates.login_streak = false;
      });
  }

  loadMealsLeaderboard(): void {
    this.loadingStates.total_meals = true;
    this.errors.total_meals = null;

    this.leaderboardService.getMealsLeaderboard()
      .then(entries => {
        this.leaderboardData.total_meals = entries;
        this.loadingStates.total_meals = false;
      })
      .catch(error => {
        this.errors.total_meals = 'Failed to load meals leaderboard.';
        this.loadingStates.total_meals = false;
      });
  }

  loadWorkoutsLeaderboard(): void {
    this.loadingStates.total_workouts = true;
    this.errors.total_workouts = null;

    this.leaderboardService.getWorkoutsLeaderboard()
      .then(entries => {
        this.leaderboardData.total_workouts = entries;
        this.loadingStates.total_workouts = false;
      })
      .catch(error => {
        this.errors.total_workouts = 'Failed to load workouts leaderboard.';
        this.loadingStates.total_workouts = false;
      });
  }

  changeCategory(category: string): void {
    this.activeCategory = category;

    // Load data for the selected category if not already loaded
    switch(category) {
      case 'login_streak':
        if (this.leaderboardData.login_streak.length === 0) {
          this.loadLoginStreakLeaderboard();
        }
        break;
      case 'total_meals':
        if (this.leaderboardData.total_meals.length === 0) {
          this.loadMealsLeaderboard();
        }
        break;
      case 'total_workouts':
        if (this.leaderboardData.total_workouts.length === 0) {
          this.loadWorkoutsLeaderboard();
        }
        break;
    }
  }

  get currentLeaderboard(): LeaderboardEntry[] {
    return this.leaderboardData[this.activeCategory as keyof typeof this.leaderboardData];
  }

  get isLoading(): boolean {
    return this.loadingStates[this.activeCategory as keyof typeof this.loadingStates];
  }

  get error(): string | null {
    return this.errors[this.activeCategory as keyof typeof this.errors];
  }

  getCategoryLabel(category: string): string {
    const labels: { [key: string]: string } = {
      'login_streak': 'Login Streak',
      'total_meals': 'Total Meals',
      'total_workouts': 'Total Workouts'
    };
    return labels[category] || category;
  }
}