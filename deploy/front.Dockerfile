FROM node:11 as build-deps

WORKDIR /usr/src/app
COPY package.json yarn.lock bsconfig.json ./
COPY .env .env.production ./
COPY front ./front

RUN yarn
ENV NODE_ENV=production
RUN yarn build

FROM abiosoft/caddy 

COPY --from=build-deps /usr/src/app/dist /www/front
COPY ./deploy/Caddyfile /etc/Caddyfile
EXPOSE 80