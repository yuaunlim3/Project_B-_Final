import { createReducer, on, Action } from '@ngrx/store';
import { addMeal, loadMeals, resetMeals } from './meal_info.action';
import { initialMealsInfoState, MealsInfoState } from './meal_info.state';


const _mealsInfoReducer = createReducer(
    initialMealsInfoState,
    on(addMeal, (state, { meal }) => ({
        ...state,
        meals: [...state.meals, meal],
    })),
    on(loadMeals, (state, { meals }) => ({
        ...state,
        meals: meals,
    })),
    on(resetMeals, () => initialMealsInfoState)
);

export function mealsInfoReducer(state: MealsInfoState | undefined, action: Action) {
    return _mealsInfoReducer(state, action);
} 