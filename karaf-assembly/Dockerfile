FROM debian:buster-slim

LABEL AUTHOR="Michael Lux (michael.lux@aisec.fraunhofer.de)"

# Fix for installing openjdk on debian-slim
RUN mkdir -p /usr/share/man/man1
# Install JDK
RUN apt-get update -qq && apt-get install -qq openjdk-11-jdk

# Install socat for UNIX socket debugging and ps command for karaf scripts
RUN apt-get update -qq && apt-get install -qq socat procps

# Optional: Install oh-my-zsh for a better shell (~ 20 MiB overhead)
RUN apt-get update -qq && apt-get install -qq wget git zsh fonts-powerline \
  && wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh || true \
  # Set nice theme
  && sed -ie 's/^ZSH_THEME=".*"$/ZSH_THEME="agnoster"/' ~/.zshrc \
  # Disable automatic update
  && sed -ie 's/^# DISABLE_AUTO_UPDATE$/DISABLE_AUTO_UPDATE/' ~/.zshrc \
  # Remove wget and git, Cleanup
  && apt-get remove --purge -qq wget git && apt-get autoremove --purge -qq \
  # Inject karaf console start command into zsh history
  && echo ": 0:0;bin/client" > ~/.zsh_history

# Install docker using get.docker.com script
ADD build/assembly/bin /root/bin/
RUN sh /root/bin/get-docker.sh

# Adding the actual core platform files to /root
ADD build/assembly /root/

# Making karaf start script and docker CLI executable
RUN chmod 0755 /root/bin/karaf

WORKDIR "/root"

# Ports to expose
EXPOSE 8181
EXPOSE 8443
EXPOSE 29998
EXPOSE 5005
EXPOSE 1099
EXPOSE 1098
EXPOSE 9292

ENTRYPOINT ["/root/bin/karaf"]
