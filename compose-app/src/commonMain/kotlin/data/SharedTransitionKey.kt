package top.ltfan.labailearn.data

import top.ltfan.labailearn.ui.Route
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class SharedTransitionKey(val key: Any, val tag: Any)

enum class SharedTransitionType { Container, Title }

@OptIn(ExperimentalUuidApi::class)
fun Uuid.transitionKeyOf(tag: SharedTransitionType) = SharedTransitionKey(this, tag)
fun Route.transitionKeyOf(tag: Any) = SharedTransitionKey(this, tag)
