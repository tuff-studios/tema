package dev.craftwarestudios.tema.core

import java.util.UUID

interface ElementDefinition {
    /**
     * Уникальный идентификатор элемента для идентификации в диалоговом API.
     */
    val elementId: UUID
}
