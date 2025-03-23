import { Injectable } from "@angular/core";
import { Store } from "@ngrx/store";
import { resetMeals } from "../ngRx/meal_info.action";

@Injectable({
  providedIn: 'root',
})
export class DataServices {
  private lastResetDate!: string | null

  constructor(private store: Store) {}

  setDate(name:string) {
    this.lastResetDate = new Date().toISOString().split('T')[0];
    localStorage.setItem(`lastResetDate_${name}`, this.lastResetDate);
  }

  checkAndReset(name:string) {
    const today = new Date().toISOString().split('T')[0];
    if (localStorage.getItem(`lastResetDate_${name}`)) {
      this.lastResetDate = localStorage.getItem(`lastResetDate_${name}`);
    } else {
      this.setDate(name)
    }
    if (this.lastResetDate !== today) {
      this.store.dispatch(resetMeals());
      this.lastResetDate = today;
      localStorage.setItem(`lastResetDate_${name}`, this.lastResetDate);
    }
  }
}
