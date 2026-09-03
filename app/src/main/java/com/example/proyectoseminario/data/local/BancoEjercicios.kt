package com.example.proyectoseminario.data.local

object BancoEjercicios {

    fun generarNodos(): List<NodoCamino> = listOf(
        NodoCamino(
            id = 1,
            titulo = "Fundamentos de Álgebra",
            descripcion = "Ecuaciones de primer grado y simplificación",
            areaMatematica = "Álgebra",
            nivelOrden = 1,
            estaDesbloqueado = true,
            estaCompletado = false,
            nodoPrerrequisitoId = null
        ),
        NodoCamino(
            id = 2,
            titulo = "Ecuaciones Cuadráticas",
            descripcion = "Factorización y fórmula general",
            areaMatematica = "Álgebra",
            nivelOrden = 2,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = 1
        ),
        NodoCamino(
            id = 3,
            titulo = "Introducción al Cálculo",
            descripcion = "Límites y derivadas básicas",
            areaMatematica = "Cálculo",
            nivelOrden = 3,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = 2
        ),
        NodoCamino(
            id = 4,
            titulo = "Factorización y Fracciones Algebraicas",
            descripcion = "Factorizar polinomios y simplificar fracciones complejas",
            areaMatematica = "Álgebra",
            nivelOrden = 4,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = 3
        ),
        NodoCamino(
            id = 5,
            titulo = "Ecuaciones, Desigualdades y Números Complejos",
            descripcion = "Resolver desigualdades y operar con complejos",
            areaMatematica = "Álgebra",
            nivelOrden = 5,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = 4
        ),
        NodoCamino(
            id = 6,
            titulo = "Geometría Plana y Teorema de Pitágoras",
            descripcion = "Figuras, áreas, perímetros y triángulos rectángulos",
            areaMatematica = "Geometría",
            nivelOrden = 6,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = 5
        )
    )

    fun generarEjercicios(): List<Ejercicio> {
        return (1..6).flatMap { nodoId ->
            (1..20).map { d ->
                when (nodoId) {
                    1 -> algebraLineal(d, nodoId)
                    2 -> cuadraticas(d, nodoId)
                    3 -> calculo(d, nodoId)
                    4 -> factorizacion(d, nodoId)
                    5 -> complejosYDesigualdades(d, nodoId)
                    6 -> geometria(d, nodoId)
                    else -> throw IllegalArgumentException("Nodo no soportado: $nodoId")
                }
            }
        }
    }

    private fun crearEjercicio(
        id: Int,
        nodoId: Int,
        enunciado: String,
        respuestaCorrecta: String,
        distractores: List<String>,
        explicacion: String,
        dificultad: Int
    ): Ejercicio {
        val opciones = (distractores + respuestaCorrecta).shuffled()
        val indice = opciones.indexOf(respuestaCorrecta)
        return Ejercicio(
            id = id,
            nodoId = nodoId,
            enunciado = enunciado,
            opcionA = opciones[0],
            opcionB = opciones[1],
            opcionC = opciones[2],
            opcionD = opciones[3],
            respuestaCorrecta = indice,
            dificultad = dificultad,
            explicacion = explicacion
        )
    }

    // NODO 1: Fundamentos de Álgebra
    private fun algebraLineal(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (d) {
            in 1..5 -> {
                val a = d + 1
                val x = d
                val b = d * 2
                val c = a * x + b
                val enunciado = "Resuelve: ${a}x + $b = $c"
                val correcta = "x = $x"
                val distractores = listOf("x = ${x + 1}", "x = ${x - 1}", "x = ${c / a}")
                val explicacion = "Restamos $b y dividimos entre $a: x = ($c - $b) / $a = $x."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 6..10 -> {
                val a = d - 4
                val x = d
                val b = d * 3
                val c = a * x - b
                val enunciado = "Resuelve: ${a}x - $b = $c"
                val correcta = "x = $x"
                val distractores = listOf("x = ${x + 1}", "x = ${x - 2}", "x = ${(c + b) / a}")
                val explicacion = "Sumamos $b y dividimos entre $a: x = ($c + $b) / $a = $x."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 11..15 -> {
                val a = 2
                val b = d
                val c = 3
                val x = d - 2
                val ladoDerecho = a * x + b
                val enunciado = "Resuelve: ${a}x + $b = ${c}x + ${ladoDerecho - c * x}"
                val correcta = "x = $x"
                val distractores = listOf("x = ${x + 1}", "x = ${x - 1}", "x = $c")
                val explicacion = "Pasamos términos: 2x - 3x = ${ladoDerecho - c * x} - $b, entonces x = $x."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            else -> {
                val a = d - 5
                val numerador = d * 2
                val x = d - 8
                val denominador = 2
                val c = numerador + (a * x) / denominador
                val enunciado = "Resuelve: ($a x + $numerador) / $denominador = $c"
                val correcta = "x = $x"
                val distractores = listOf("x = ${x + 1}", "x = ${x - 1}", "x = ${c * denominador - numerador}")
                val explicacion = "Multiplicamos por $denominador: $a x + $numerador = ${c * denominador}, luego x = $x."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
        }
    }

    // NODO 2: Ecuaciones Cuadráticas
    private fun cuadraticas(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (d) {
            in 1..5 -> {
                val r = d
                val s = d + 1
                val b = -(r + s)
                val c = r * s
                val enunciado = "Factoriza: x² ${if (b < 0) "- ${-b}" else "+ $b"}x + $c = 0"
                val correcta = "(x - $r)(x - $s)"
                val distractores = listOf("(x + $r)(x - $s)", "(x - ${r + 1})(x - $s)", "(x + $r)(x + $s)")
                val explicacion = "Buscamos números que sumen ${-(b)} y multipliquen $c: $r y $s."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 6..10 -> {
                val r = d - 3
                val enunciado = "Resuelve: x² - ${r * r} = 0"
                val correcta = "x = ±$r"
                val distractores = listOf("x = $r", "x = -$r", "x = ±${r + 1}")
                val explicacion = "x² = ${r * r}, por lo que x puede ser $r o -$r."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 11..15 -> {
                val r = d - 8
                val s = d - 6
                val b = -(r + s)
                val c = r * s
                val enunciado = "Resuelve: x² ${if (b < 0) "- ${-b}" else "+ $b"}x + $c = 0"
                val correcta = "x = $r, x = $s"
                val distractores = listOf("x = ${r + 1}, x = $s", "x = -$r, x = -$s", "x = ${r + 2}, x = ${s + 1}")
                val explicacion = "Factorizamos (x - $r)(x - $s) = 0, por lo que x = $r o x = $s."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            else -> {
                val a = 2
                val b = d
                val c = d - 8
                val enunciado = "Resuelve: ${a}x² + ${b}x + $c = 0 (usa fórmula general)"
                val discriminante = b * b - 4 * a * c
                val correcta = if (discriminante >= 0) "x = ${-b} ± √$discriminante / ${2 * a}" else "sin solución real"
                val distractores = listOf("x = $b ± √$discriminante", "x = ${-b} / ${2 * a}", "x = 1, x = $c")
                val explicacion = "x = (-$b ± √$discriminante) / ${2 * a}."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
        }
    }

    // NODO 3: Introducción al Cálculo
    private fun calculo(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (d) {
            in 1..5 -> {
                val x = d
                val m = 2
                val b = 3
                val enunciado = "Límite de ${m}x + $b cuando x tiende a $x"
                val correcta = "${m * x + b}"
                val distractores = listOf("${m * x}", "$b", "${m * x + b + 1}")
                val explicacion = "Sustituimos x = $x: ${m}($x) + $b = ${m * x + b}."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 6..10 -> {
                val n = d - 4
                val enunciado = "Derivada de f(x) = x^$n"
                val correcta = "${n}x^${n - 1}"
                val distractores = listOf("x^${n - 1}", "${n - 1}x^$n", "x^$n")
                val explicacion = "Aplicamos d/dx(xx(xⁿ) = n xⁿ⁻¹: $n x^${n - 1}."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 11..15 -> {
                val a = d - 8
                val n = 2
                val b = 5
                val enunciado = "Derivada de f(x) = ${a}x² + ${b}x"
                val correcta = "${2 * a}x + $b"
                val distractores = listOf("${a}x + $b", "${2 * a}x", "$b")
                val explicacion = "Derivamos término a término: ${2 * a}x + $b."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            else -> {
                val a = d - 10
                val b = 2
                val enunciado = "Límite cuando x tiende a $b de (x² - $a)"
                val correcta = "${b * b - a}"
                val distractores = listOf("${b * b}", "${a - b * b}", "0")
                val explicacion = "Sustituimos x = $b: $b² - $a = ${b * b - a}."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
        }
    }

    // NODO 4: Factorización y Fracciones Algebraicas
    private fun factorizacion(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (d) {
            in 1..5 -> {
                val a = d + 1
                val b = d * 2
                val enunciado = "Factoriza: ${a}x² + ${a * d}x"
                val correcta = "${a}x(x + $d)"
                val distractores = listOf("x(x + $d)", "${a}(x + $d)", "x² + ${d}x")
                val explicacion = "Sacamos el factor común ${a}x: ${a}x(x + $d)."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 6..10 -> {
                val n = d - 2
                val enunciado = "Factoriza: x² - ${n * n}"
                val correcta = "(x - $n)(x + $n)"
                val distractores = listOf("(x - $n)²", "(x + $n)²", "x(x - $n)")
                val explicacion = "Diferencia de cuadrados: (x - $n)(x + $n)."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 11..15 -> {
                val a = d - 8
                val b = d - 7
                val c = a * b
                val enunciado = "Factoriza: x² + ${a + b}x + $c"
                val correcta = "(x + $a)(x + $b)"
                val distractores = listOf("(x - $a)(x - $b)", "(x + ${a + 1})(x + $b)", "(x + $a)(x - $b)")
                val explicacion = "Buscamos números que sumen ${a + b} y multipliquen $c: $a y $b."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            else -> {
                val a = d - 12
                val enunciado = "Simplifica: (x² - ${a * a}) / (x - $a)"
                val correcta = "x + $a"
                val distractores = listOf("x - $a", "x² - $a", "1")
                val explicacion = "x² - ${a * a} = (x - $a)(x + $a). Cancelamos (x - $a)."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
        }
    }

    // NODO 5: Ecuaciones, Desigualdades y Números Complejos
    private fun complejosYDesigualdades(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (d) {
            in 1..5 -> {
                val a = d + 1
                val b = d * 2
                val c = a * d - b
                val enunciado = "Resuelve: ${a}x - $b > $c"
                val correcta = "x > $d"
                val distractores = listOf("x < $d", "x ≥ $d", "x = $d")
                val explicacion = "Sumamos $b: ${a}x > ${c + b}. Dividimos entre $a: x > $d."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 6..10 -> {
                val a = d - 4
                val b = d
                val c = d - 1
                val enunciado = "Resuelve: x² - ${a + c}x + ${a * c} ≤ 0"
                val correcta = "$a ≤ x ≤ $c"
                val distractores = listOf("x ≤ $a", "x ≥ $c", "$c ≤ x ≤ $a")
                val explicacion = "x² - ${a + c}x + ${a * c} = (x - $a)(x - $c). La solución es $a ≤ x ≤ $c."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 11..15 -> {
                val real = d - 8
                val img = d - 10
                val enunciado = "Si z = $real + ${img}i y w = 2 + 3i, encuentra z + w"
                val correcta = "${real + 2} + ${img + 3}i"
                val distractores = listOf("${real + 2} + ${img}i", "${real} + ${img + 3}i", "${real + img} + ${real + 2}i")
                val explicacion = "Sumamos parte real e imaginaria: ($real + 2) + (${img} + 3)i."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            else -> {
                val a = d - 12
                val b = d - 14
                val enunciado = "Multiplica: ($a + 2i)(3 + ${b}i)"
                val real = a * 3 - 2 * b
                val img = a * b + 2 * 3
                val correcta = "$real + ${img}i"
                val distractores = listOf("${a * 3} + ${img}i", "$real + ${a * b}i", "${a + 3} + ${2 + b}i")
                val explicacion = "Usamos (a+bi)(c+di) = (ac - bd) + (ad + bc)i."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
        }
    }

    // NODO 6: Geometría Plana y Teorema de Pitágoras
    private fun geometria(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (d) {
            in 1..5 -> {
                val base = d + 2
                val altura = d + 1
                val enunciado = "Calcula el área de un rectángulo de base $base y altura $altura"
                val correcta = "${base * altura}"
                val distractores = listOf("${base + altura}", "${2 * (base + altura)}", "${base * altura + 1}")
                val explicacion = "Área = base × altura = $base × $altura = ${base * altura}."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 6..10 -> {
                val a = d - 3
                val b = d - 1
                val enunciado = "Perímetro de un rectángulo de lados $a y $b"
                val correcta = "${2 * (a + b)}"
                val distractores = listOf("${a + b}", "${a * b}", "${2 * a + b}")
                val explicacion = "Perímetro = 2(a + b) = 2($a + $b) = ${2 * (a + b)}."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            in 11..15 -> {
                val a = d - 8
                val b = d - 6
                val hip = a * a + b * b
                val enunciado = "Halla la hipotenusa de un triángulo rectángulo con catetos $a y $b"
                val correcta = "√$hip"
                val distractores = listOf("$hip", "√${hip + 1}", "${a + b}")
                val explicacion = "Por Pitágoras: c² = $a² + $b² = $hip, entonces c = √$hip."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
            else -> {
                val x1 = 0
                val y1 = 0
                val x2 = d - 12
                val y2 = d - 10
                val dist = x2 * x2 + y2 * y2
                val enunciado = "Distancia entre los puntos ($x1,$y1) y ($x2,$y2)"
                val correcta = "√$dist"
                val distractores = listOf("$dist", "√${dist + 1}", "${x2 + y2}")
                val explicacion = "d = √[(x2 - x1)² + (y2 - y1)²] = √$dist."
                crearEjercicio(id, nodoId, enunciado, correcta, distractores, explicacion, d)
            }
        }
    }
}
