import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { Completed, Planned, TotalWorkoutData, User, Workouts } from "../model/workout.model";

Injectable()
export class WorkoutService{
    private http = inject(HttpClient)

    getWorkoutInfo(workout:Workouts):Promise<Workouts>{

        return firstValueFrom(this.http.post<Workouts>('app/workout',workout))
    }

    completedPlan(info:Completed){
        return firstValueFrom(this.http.post<Workouts>('app/completedWorkout',info))
    }

    getPlanned(name: string): Promise<Planned> {
        return firstValueFrom(this.http.get<Planned>('app/getAll', {
            params: new HttpParams().set('name', name)
        }));
    }

    getWorkoutChartData(name:string):Promise<TotalWorkoutData[]>{
        return firstValueFrom(this.http.get<TotalWorkoutData[]>('app/getTotalWorkouts', {
            params: new HttpParams().set('name', name)
        }));
    }
    
}