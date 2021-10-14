FROM clojure:lein as builder
RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash -
RUN apt-get install -y nodejs 
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY resources/public ./resources/public/
COPY src ./src/
COPY figwheel-main.edn prod.cljs.edn project.clj package.json webpack.config.js ./
RUN npm install
RUN npx webpack
RUN lein deps
RUN lein fig:prod

FROM nginx:latest
COPY conf/default.conf.template /etc/nginx/templates/default.conf.template
COPY conf/nginx.conf /etc/nginx/nginx.conf
COPY --from=builder /usr/src/app/resources/public /usr/share/nginx/html
RUN chmod 755 /usr/share/nginx/html/*
