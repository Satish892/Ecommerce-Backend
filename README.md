server:
  port: 8080

# Database Configuartion
spring:
  datasource:
    url: "url"
    username: "root"
    password: "Satish@31"
    driver-class-name: com.mysql.cj.jdbc.Driver

# JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

#File upload config
  servlet:
    multipart:
      max-file-size: 2MB
      max-request-size: 5MB

#Role Id
role.admin.id: "unique id for admin role"
role.normal.id: "unique id for normal role"

# Google Login
googleClientId: "google-client-id"
googleSecret: "google-secret"

# JWT Secret Key
application:
  security:
    jwt:
     secret-key: "secret-key"
     expiration: "jwt-token-expiry-time"
     refresh-token:
      expiration: "refresh-token-expiry-time"

#AWS S3
aws:
  s3:
    accessKey: "s3-accessKey"
    secretKey: "s3-secretKey"
    bucketName: "s3-bucketName"
    region: "aws-bucket-region"
    user-image-path: "s3 bucket directory for storing user images"
    products-image-path: "s3 bucket directory for storing product images"
    categories-image-path: "s3 bucket directory for storing category images"
    
