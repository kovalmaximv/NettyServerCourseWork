# Курсовой проект по компьютерным сетям
# Система распределенного казино

Данный репозиторий является main сервером распределеннного казино. 
Система работает исключительно на протоколе tcp. 
В целом, система состоит из: main сервера, 
n игровых серверов (на данный момент 2), мобильного приложения на iOS.

Возможности системы (помимо тривиальных, по типу игр):
1) Очереди в играх
2) Система уведомлений
3) Внутриигровой чат
4) Динамическое увеличение кол-ва игр

В обязанности main сервера входит: 
1) Учет данных пользователя (логин/регистрация, баланс)
2) Разбор комманд пользователя, переадресация на игровые сервера
3) Нотификация пользователя по окончанию игры
4) Поддержка игрового чата

---
# Блок для разработчиков

Команды main сервера

- Регистрация
> registration **username** **password**
- Логин
> login **username** **password**
- Присоединение к игре
> game **gamename** **token** **bet** **ставка**
- Баланс
> balance **token**
- Чат
> chat **token** **to** **message**

