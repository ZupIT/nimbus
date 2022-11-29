package br.com.zup.nimbus.core.dependency

class UpdateError(errors: List<Throwable>): Error() {
  override val message = "There were errors while updating the dependency graph: " +
    errors.joinToString("\n\t") { it.message ?: "" }
}
