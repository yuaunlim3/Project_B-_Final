
export interface MealsInfo{
    food:string
    found?:string
    calories:number
    fat:number
    sugars:number
    protein: number
    name:string
}

export interface Meals{
    type: string
    food:string
    amount:number
    serving_size:string
}


export interface TotalMealData{
    date:string
    total_calories:number
    total_proteins:number
    total_sugar:number
    total_fats:number
}

export interface Advice{
    advice:string
}

