FROM debian:buster-slim

LABEL AUTHOR="Michael Lux (michael.lux@aisec.fraunhofer.de)"

# Fix for installing openjdk on debian-slim
RUN mkdir -p /usr/share/man/man1
# Install JDK
RUN apt-get update -qq && apt-get install -qq openjdk-11-jdk

# Install tools for nodejs/yarn setup
RUN apt-get update -qq && apt-get install -qq bash sudo curl gnupg
# Install nodejs 12.x
RUN curl -sL https://deb.nodesource.com/setup_12.x  | bash - && apt-get install -qq nodejs
# Install yarn
RUN curl -sL https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - \
    && echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list \
    && apt-get update && apt-get install yarn

COPY run.sh .
RUN chmod +x run.sh

ENTRYPOINT ["/run.sh"]
CMD ["build", "check", "dockerize", "--no-daemon", "--parallel", "-PdockerTag=develop"]