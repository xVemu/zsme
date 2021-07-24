package pl.vemu.zsme.util

interface EntityMapper<Entity, Model> {

    fun mapFromEntity(entity: Entity): Model

    fun mapToEntity(model: Model): Entity

    fun mapFromEntityList(entities: List<Entity>): List<Model>

    fun mapToEntityList(models: List<Model>): List<Entity>

}