import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { LeaderboardEntry } from "../model/model";
@Injectable()
export class LeaderboardService {
  constructor(private http: HttpClient) {}

  getMealsLeaderboard(): Promise<LeaderboardEntry[]> {
    return firstValueFrom(
      this.http.get<LeaderboardEntry[]>('/app/leaderboard/meals')
    );
  }

  getWorkoutsLeaderboard(): Promise<LeaderboardEntry[]> {
    return firstValueFrom(
      this.http.get<LeaderboardEntry[]>('/app/leaderboard/workouts')
    );
  }

  getLoginStreakLeaderboard(): Promise<LeaderboardEntry[]> {
    return firstValueFrom(
      this.http.get<LeaderboardEntry[]>('/app/leaderboard/login-streak')
    );
  }
}