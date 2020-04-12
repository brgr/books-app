
# Notes

The notes I write here don't necessarily have much to do with my actual project. Sometimes
they are just indirectly linked to it.

## SSH Key: With or without passphrase?

When automating access to a server, which means that the private SSH key needs to be written
somewhere (and also its passphrase, if it has one), then there is actually no real advantage
in using a passphrase for the key - i.e., the private key alone is actually enough.

For a discussion on this, see [here][1].

[1]: https://unix.stackexchange.com/questions/90853/how-can-i-run-ssh-add-automatically-without-a-password-prompt