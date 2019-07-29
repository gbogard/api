FROM node:11 as build-deps

WORKDIR /usr/src/app
COPY package.json yarn.lock bsconfig.json ./
COPY .env .env.production ./
COPY front ./front

RUN yarn
ENV NODE_ENV=production
RUN yarn build

FROM nginx:stable-alpine 

RUN rm /etc/nginx/conf.d/default.conf

COPY --from=build-deps /usr/src/app/dist /usr/share/nginx/html
COPY  ./deploy/nginx.conf /etc/nginx
EXPOSE 80