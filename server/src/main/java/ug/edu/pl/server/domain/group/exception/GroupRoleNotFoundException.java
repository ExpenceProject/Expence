package ug.edu.pl.server.domain.group.exception;

public class GroupRoleNotFoundException extends RuntimeException {
  public GroupRoleNotFoundException(String name) {
    super("GroupRole with name '%s' not found".formatted(name));
  }
}
