package com.example.dentaku

import org.scalajs.dom
import scala.scalajs.js
import com.raquo.laminar.api.L.{*, given}
import scala.util.Try
import scala.util.Success
import scala.util.Failure

@main def main =
  documentEvents.onDomContentLoaded.foreach { _ =>
    val appContainer: dom.Element = dom.document.body

    val bus = EventBus[String]
    val observer = bus.writer
    val observable = bus.events
      .foldLeft[Either[String, String]](Right("")) {
        case (Left(msg), "AC") => Right("")
        case (Left(msg), _)    => Left(msg)
        case (Right(exp), cmd) =>
          cmd match
            case "AC" => Right("")
            case "=" =>
              Try(js.eval(exp)) match
                case Success(r)  => Right(r.toString)
                case Failure(ex) => Left(ex.getMessage)
            case "+" | "-" | "*" | "/" =>
              Right(if exp == "" then cmd else s"${exp} ${cmd} ")
            case _ => Right(s"${exp}${cmd}")
      }
      .map {
        case Left(msg)  => msg
        case Right(exp) => exp
      }

    val appElement: Div = div(
      width := "300px",
      div(
        padding := "4px",
        display := "flex",
        flexDirection := "row",
        input(
          margin := "4px",
          flexGrow := 1,
          border := "1px solid silver",
          readOnly := true,
          typ := "text",
          value <-- observable
        ),
        button(
          margin := "4px",
          "AC",
          onClick.mapTo("AC") --> observer
        )
      ),
      div(
        padding := "4px",
        display := "flex",
        flexDirection := "column",
        js.Array(
          js.Array("1", "2", "3", "/"),
          js.Array("4", "5", "6", "*"),
          js.Array("7", "8", "9", "-"),
          js.Array("0", ".", "=", "+")
        ).map { row =>
          div(
            display := "flex",
            flexDirection := "row",
            row.map { cell =>
              button(
                flexGrow := 1,
                margin := "4px",
                cell,
                onClick.mapTo(cell) --> observer
              )
            }
          )
        }
      )
    )
    render(appContainer, appElement)
  }(unsafeWindowOwner)
