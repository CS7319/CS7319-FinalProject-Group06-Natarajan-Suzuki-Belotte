# stage: build
FROM node:lts-alpine3.23 AS build

WORKDIR /app
COPY eventual.frontend/package*.json .
RUN npm clean-install
COPY eventual.frontend .

RUN npm run build -- --configuration production

# stage: serve
FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist/eventual.frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
