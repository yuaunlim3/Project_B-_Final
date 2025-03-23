import { createAction, props } from '@ngrx/store';
import { MealsInfo } from '../model/meal.model';


export const addMeal = createAction(
    '[Meals Info] Add Meal',
    props<{ meal: MealsInfo }>()
);


export const loadMeals = createAction(
    '[Meals Info] Load Meals',
    props<{ meals: MealsInfo[] }>()
);

export const resetMeals = createAction('[Meals Info] Reset Meals');