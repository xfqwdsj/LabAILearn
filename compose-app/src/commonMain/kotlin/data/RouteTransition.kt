package top.ltfan.labailearn.data

import top.ltfan.labailearn.ui.Route

data class RouteTransitionKey(val route: Route, val tag: Any)

enum class RouteTransitionType { Container, Title }

fun Route.transitionKeyOf(tag: Any) = RouteTransitionKey(this, tag)
