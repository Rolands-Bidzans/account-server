# 📘 Before Running Tests Run Additional Services
Ensure Docker is installed and running on your system. These steps will guide you through running the required services for your application.

## 🐇 Start RabbitMQ
RabbitMQ is used for message brokering between microservices.
```bash
docker run --rm -it -d --network host -p 15672:15672 -p 5672:5672 rabbitmq:3.10.5-management
```

## 📦 Start the Orders Service
This service manages customer orders.
```bash
docker run --rm -it -d --network host -p 8083:8083 rolandstech/order
```

## 🔔 Start the Notifications Service
This service handles sending notifications to users.
```bash
docker run --rm -it -d --network host -p 8082:8082 rolandstech/notification-service
```

## 📊 Check Running Containers
To confirm all services are running, use:
```bash
docker ps
```

## 🛑 Stop All Services
To stop and remove all containers:
```bash
docker stop $(docker ps -q)

