FROM node:23 AS angular-builder

WORKDIR /src

COPY FrontEnd/*.json .
COPY FrontEnd/public public
COPY FrontEnd/src src

RUN npm ci
RUN npm i -g @angular/cli

RUN ng build

FROM eclipse-temurin:23-jdk AS sb-builder

WORKDIR /src

COPY BackEnd/mvnw .
COPY BackEnd/pom.xml .
COPY BackEnd/src src
COPY BackEnd/.mvn .mvn


COPY --from=angular-builder /src/dist/project-b-frontend/browser/* /src/main/resources/static/

RUN chmod a+x mvnw

RUN ./mvnw package -Dtest.skip=true -e || (echo "Maven build failed. See error above." && false)


FROM eclipse-temurin:23-jre 
WORKDIR /app

COPY --from=sb-builder /src/target/projectB-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/projectb
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=password
ENV SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/projectB
ENV SPRING_DATA_REDIS_HOST=localhost
ENV SPRING_DATA_REDIS_PORT=6379
ENV SPRING_DATA_REDIS_PASSWORD=password
ENV SPRING_DATA_REDIS_USERNAME=default
ENV SPRING_DATA_REDIS_DATABASE=0
ENV NUTRITIONIX_API_KEY=your_key_here
ENV NUTRITIONIX_API_ID=your_id_here
ENV WEATHERBIT_API_KEY=your_key_here
ENV WEATHERBIT_COUNTRY=Singapore
ENV MAILJET_API_KEY=your_key_here
ENV MAILJET_API_SECRET=your_secret_here
ENV OPENAI_API_KEY=your_key_here
ENV STRIPE_PUBLIC_KEY=your_public_key_here
ENV STRIPE_SECRET_KEY=your_secret_key_here
ENV APP_BASE_URL=http://localhost:8080
ENV JWT_SECRET=your_jwt_secret_here
ENV JWT_EXPIRATION=86400000
ENV PORT=8080

EXPOSE ${PORT}
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar