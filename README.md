# EventSphere — Monorepo Setup

## Structure
```
eventsphere/
  backend/     -> Spring Boot (Java 17)
  frontend/    -> Angular
  docker-compose.yml   -> MySQL, Redis, Kafka
```

## First-time setup (do once per PC)
1. Install JDK 17, Node LTS, Docker Desktop, Git, IDE.
2. `git clone <your-repo-url>`
3. `cd eventsphere`
4. Copy `backend/.env.example` -> create local secrets (see below).
5. `docker compose up -d` — starts MySQL, Redis, Kafka.

## Daily workflow (every session, both PCs)
```
git pull                          # start of session
docker compose up -d              # start infra
cd backend && ./mvnw spring-boot:run     # terminal 1
cd frontend && ng serve --proxy-config proxy.conf.json   # terminal 2

# ... work ...

git add .
git commit -m "message"
git push                          # end of session
```

## Secrets (never committed)
Create `backend/src/main/resources/application-local.yml` (gitignored) on each PC with:
```yaml
jwt:
  secret: your-actual-secret-here
```
Or set `JWT_SECRET` as an OS environment variable.

## Ports
- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- MySQL: localhost:3306 (user: eventsphere_user / pass: eventsphere_pass)
- Redis: localhost:6379
- Kafka: localhost:9092

## Production build (single deployable jar option)
```
cd frontend && ng build
# copy frontend/dist/<project-name>/* into backend/src/main/resources/static/
cd backend && ./mvnw clean package
# run: java -jar target/eventsphere-backend.jar
```
