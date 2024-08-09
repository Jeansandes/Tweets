
FROM openjdk:23-ea-17-jdk-bullseye

# Definindo o diretório de trabalho dentro da imagem Docker
WORKDIR /app

# Copiando o arquivo JAR para dentro da imagem Docker
COPY target/tweet-0.0.1-SNAPSHOT.jar /app/app.jar

# Expondo a porta 8080 (se necessário)
EXPOSE 8080

# Comando para executar o JAR na inicialização do contêiner
CMD ["java", "-jar", "app.jar"]
