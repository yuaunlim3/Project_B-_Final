export interface Workouts{
    total_calories: any
    index: number
    details:string
    duration:number
    type:string
    status:string
    date:string
    calories:number
    name:string
}


export interface User {
    name: string;
    workouts: Workouts[];
  }


export interface Completed{
    name:string;
    index:number;
}

export interface Planned{
    workouts:Workouts[]
}

export interface TotalWorkoutData{
    date:string
    total_calories:number
}
