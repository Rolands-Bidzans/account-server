# 📘 Before Running Tests Run Additional Services
## 🐇 Start RabbitMQ
```bash
docker run --rm -it -d --network host -p 15672:15672 -p 5672:5672 rabbitmq:3.10.5-management
```

## 📦 Start the Orders Service
```bash
docker run --rm -it -d --network host -p 8083:8083 rolandstech/order
```

## 🔔 Start the Notifications Service
```bash
docker run --rm -it -d --network host -p 8082:8082 rolandstech/notification-service
```

## 📊 Check Running Containers
```bash
docker ps
```

## 🛑 Stop All Services
```bash
docker stop $(docker ps -q)

