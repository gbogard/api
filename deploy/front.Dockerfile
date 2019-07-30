FROM node:11 as build-deps

WORKDIR /usr/src/app
COPY package.json yarn.lock bsconfig.json ./
COPY .env .env.production ./
COPY front ./front

RUN yarn
ENV NODE_ENV=production
RUN yarn build

FROM gbogard/caddy

COPY --from=build-deps /usr/src/app/dist /www/front
COPY ./deploy/Caddyfile /etc/Caddyfile
ENV ACME_AGREE true
EXPOSE 80