export interface LoginDetails {
    email: string
    password: string
}

export interface login {
    name: string
    token: string
}

export interface UserDetails {
    name: string
    email: string
    password: string
    height: number
    weight: number
    last_login: string
    created_at: string
    login_streak: number
    age: number
    target: string
    target_cal: number
    gender: string
    activity_level: string
    subscription: string
    paid_date:string
    missed:number
}


export interface Weather {
    date: string
    maxTemp: number
    minTemp: number
    avgTemp: number
    description: string
    image: string
}

export interface PaymentInfo {
    status: string
    url: string
    message: string
    id: string

}

export interface VerifyPayment {
    id: string
    email: string
}

export interface VerifyCode {
    code: string
    email: string
}

export interface ResetPassword {
    email: string
    password: string
}

export interface Achievement {
    id: number;
    achievement: string;
    description: string;
    dateEarned: string;
}


export interface ToChange {
    item: string
    change: string
    name: string
}

export interface LeaderboardEntry {
    name: string;
    score: number;
    rank: number;
}


