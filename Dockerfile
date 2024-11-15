# Usa una imagen base de JDK para Kotlin
FROM eclipse-temurin:17-jdk-alpine

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos del proyecto al contenedor
COPY . .

# Descarga las dependencias y compila el proyecto
RUN ./gradlew clean build -x test

# Cambia "<nombre-del-jar>" por el nombre real del archivo JAR generado
CMD ["java", "-jar", "build/libs/bot-kotlin-1.0-SNAPSHOT.jar"]