# マルチステージビルド: ビルドステージ
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# pom.xmlとソースコードをコピー
COPY pom.xml .
COPY src ./src

# アプリケーションをビルド
RUN mvn clean package -DskipTests

# ランタイムステージ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# ビルドしたJARファイルをコピー
COPY --from=build /app/target/booking-management-system-*.jar app.jar

# ポート8080を公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]


