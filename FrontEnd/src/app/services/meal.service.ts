import { inject, Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { firstValueFrom } from "rxjs";
import { Advice, Meals, MealsInfo, TotalMealData } from "../model/meal.model";

@Injectable()
export class MealService {

    private http = inject(HttpClient)
    getMealInfo(meal: Meals): Promise<MealsInfo> {
        return firstValueFrom(
            this.http.post<MealsInfo>('app/getMealInfo', meal)
        );
    }

    saveMeal(meal:MealsInfo){
        return firstValueFrom(
            this.http.post<MealsInfo>('app/saveMeal', meal)
        );
    }

    getMealChartData(name:string):Promise<TotalMealData[]>{
        return firstValueFrom(
            this.http.get<TotalMealData[]>('app/getTotalMeals', {
                        params: new HttpParams().set('name', name)})
        );
    }


    getTodayMeals(name:string):Promise<MealsInfo[]>{
        return firstValueFrom(
            this.http.get<MealsInfo[]>('app/getTodayMeals', {
                        params: new HttpParams().set('name', name)})
        );
    }

    getAdvice(name:string):Promise<Advice>{
        return firstValueFrom(
            this.http.get<Advice>('app/advice', {
                        params: new HttpParams().set('name', name)})
        );
    }

}