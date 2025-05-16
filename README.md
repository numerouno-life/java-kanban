# TaskFlow: Kanban-система управления задачами

![Java](https://img.shields.io/badge/Java-21-%23ED8B00?logo=openjdk)
![Spring](https://img.shields.io/badge/Spring_Boot-3.2-%236DB33F?logo=spring)
![React](https://img.shields.io/badge/React-18-%2361DAFB?logo=react)
![MongoDB](https://img.shields.io/badge/MongoDB-7-%2347A248?logo=mongodb)

## 🚀 Основные возможности

### 📌 Управление задачами
- Drag-and-drop перемещение карточек между статусами (To Do / In Progress / Done)
- Система тегов и приоритетов
- Гибкая фильтрация и поиск

### 🖇 Работа с вложениями
- Загрузка файлов (до 50MB) с превью-генерацией
- Интеграция с AWS S3 для хранения
- Кеширование превью на клиенте (уменьшение нагрузки на сервер)

### 🔔 Система уведомлений
- Real-time оповещения через WebSocket
- История изменений с возможностью отката
- Email-уведомления для критичных действий

## 🛠 Технологический стек

### Backend
| Технология | Применение |
|------------|------------|
| Java 21 | Ядро системы |
| Spring Boot 3 | REST API |
| MongoDB | Хранение задач и истории |
| WebSocket | Real-time обновления |
