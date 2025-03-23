package VTTP.projectB.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import VTTP.projectB.JWT.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/*.js",
                        "/*.css",
                        "/*.ico",
                        "/*.json",
                        "/assets/**",
                        "/logos/**",
                        "/nav_bar_icons/**",
                        "/healthtracker/**",
                        "/user/**",
                        "/error",
                        "/app/login",
                        "/app/create",
                        "/app/forgetPassword",
                        "/app/verifyCode",
                        "/app/checkCode",
                        "/app/WeatherInfo",
                        "/app/advice",
                        "/app/payment",
                        "/app/verify",
                        "/app/leaderboard/meals",
                        "/app/leaderboard/workouts",
                        "/app/leaderboard/login-streak",
                        "/icons/**", "/images/**", "/css/**", "/js/**")
                .permitAll()
                .requestMatchers(
                        "/app/getInfo",
                        "/app/getInfoByEmail",
                        "/app/getAll",
                        "/app/workout",
                        "/app/completedWorkout",
                        "/app/getTotalWorkouts",
                        "/app/getMealInfo",
                        "/app/saveMeal",
                        "/app/getTotalMeals",
                        "/app/getTodayMeals",
                        "/app/resetPassword",
                        "/app/achievements"

                ).authenticated()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}