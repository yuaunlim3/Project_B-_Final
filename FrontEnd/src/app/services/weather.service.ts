import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { Weather } from "../model/model";

@Injectable()
export class WeatherService{
        private http = inject(HttpClient)
    
        getWeatherInfo(): Promise<Weather[]> {
            return firstValueFrom(
                this.http.get<Weather[]>('app/WeatherInfo')
            );
        }
}