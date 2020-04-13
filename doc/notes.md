
# Notes

The notes I write here don't necessarily have much to do with my actual project. Sometimes
they are just indirectly linked to it.

## Docker

Some links and small notes:

- Best practices for Dockerfile:
  https://docs.docker.com/develop/develop-images/dockerfile_best-practices/
- Common use cases for Docker compose:
  https://docs.docker.com/compose/#common-use-cases

## SSH Key: With or without passphrase?

When automating access to a server, which means that the private SSH key needs to be written
somewhere (and also its passphrase, if it has one), then there is actually no real advantage
in using a passphrase for the key - i.e., the private key alone is actually enough.

For a discussion on this, see [here][1].

## Github Actions

[Awesome Github Actions][3]

### What's installed on the Github-hosted runners

https://help.github.com/en/actions/reference/software-installed-on-github-hosted-runners

### Testing Actions Locally

Maybe [this][4] can help. Maybe not. So far I have simply created a dummy branch where
I push to until I am happy with the result of the action.

### External Github Actions

Using Github Actions that are not directly from Github is not really safe! For a 
discussion on this, see [here][2].

### Some small notes

- Breaking bash commands into multiple lines is not allowed:
  https://stackoverflow.com/questions/59954185/github-action-split-long-command-into-multiple-lines


[1]: https://unix.stackexchange.com/questions/90853/how-can-i-run-ssh-add-automatically-without-a-password-prompt
[2]: https://stackoverflow.com/questions/57916983/github-actions-are-there-security-concerns-using-an-external-action-in-a-workfl
[3]: https://github.com/sdras/awesome-actions
[4]: https://github.com/nektos/act