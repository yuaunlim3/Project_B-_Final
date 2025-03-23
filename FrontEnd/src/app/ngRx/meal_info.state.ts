import { MealsInfo } from "../model/meal.model";

export interface MealsInfoState{
    meals:MealsInfo[]
}

export const initialMealsInfoState:MealsInfoState = {
    meals:[]
} 