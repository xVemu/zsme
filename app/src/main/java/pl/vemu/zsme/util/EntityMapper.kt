package pl.vemu.zsme.util

interface EntityMapper<Entity, Model> {

    fun mapFromEntity(entity: Entity): Model

    fun mapToEntity(model: Model): Entity

}