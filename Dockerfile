# Usa una imagen base de JDK en Ubuntu
FROM eclipse-temurin:17-jdk

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos del proyecto al contenedor
COPY . .

# Otorga permisos de ejecución al script gradlew
RUN chmod +x ./gradlew

# Descarga las dependencias y compila el proyecto
RUN ./gradlew clean build -x test

CMD ["java", "-jar", "build/libs/bot-kotlin-1.0-SNAPSHOT.jar"]
