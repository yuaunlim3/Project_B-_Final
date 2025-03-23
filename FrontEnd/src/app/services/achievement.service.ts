import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Achievement } from '../model/model';

@Injectable({
    providedIn: 'root'
})
export class AchievementService {

    constructor(private http: HttpClient) { }

    getAchievements(email: string): Promise<Achievement[]> {
        return firstValueFrom(
            this.http.get<any>('/app/achievements', {
                params: new HttpParams().set('email', email)
            })
        ).then(response => {
            return response.achievements || [];
        });
    }

    getCode(email: string) {
        return firstValueFrom(this.http.get('app/forgetPassword', {
            params: new HttpParams().set('email', email)
        })).catch(error => {
            throw error
        }
        )
    }

    getAchievementDisplayName(achievementCode: string): string {
        const displayNames: { [key: string]: string } = {
            'FIRST_LOGIN': 'First Login',
            'LOGIN_STREAK_7': 'Weekly Warrior',
            'LOGIN_STREAK_30': 'Dedication Master',
            'FIRST_MEAL': 'First Meal',
            'MEAL_MASTER': 'Nutrition Expert',
            'FIRST_WORKOUT': 'Getting Started',
            'WORKOUT_WARRIOR': 'Workout Warrior',
            'WORKOUT_MASTER': 'Fitness Master',
            'CALORIE_COUNTER': 'Calorie Counter',
            'PREMIUM_MEMBER': 'Premium Member'
        };

        return displayNames[achievementCode] || achievementCode;
    }

    getAchievementIcon(achievementCode: string): string {
        const icons: { [key: string]: string } = {
            'FIRST_LOGIN': 'fa-sign-in-alt',
            'LOGIN_STREAK_7': 'fa-calendar-check',
            'LOGIN_STREAK_30': 'fa-calendar-star',
            'FIRST_MEAL': 'fa-utensils',
            'MEAL_MASTER': 'fa-apple-alt',
            'FIRST_WORKOUT': 'fa-running',
            'WORKOUT_WARRIOR': 'fa-dumbbell',
            'WORKOUT_MASTER': 'fa-medal',
            'CALORIE_COUNTER': 'fa-calculator',
            'PREMIUM_MEMBER': 'fa-crown'
        };

        return icons[achievementCode] || 'fa-award';
    }
}