package com.example.proyectoseminario.data.local

object BancoEjercicios {

    // Tipos de nodo en el mapa
    const val TIPO_LECCION = "leccion"
    const val TIPO_APLICADO = "aplicado"
    const val TIPO_BOSS = "boss"
    const val TIPO_BOSS_FINAL = "boss_final"

    // Tipos de ejercicio
    const val EJ_NORMAL = "normal"
    const val EJ_APLICADO = "aplicado"
    const val EJ_BOSS = "boss"

    const val LECCIONES_POR_TEMA = 4
    const val EJERCICIOS_POR_LECCION = 8   // pool del que se muestrean 5 por sesión
    const val APLICADOS_POR_TEMA = 8
    const val BOSS_EJERCICIOS = 10
    const val BOSS_FINAL_EJERCICIOS = 12
    const val NODOS_POR_TEMA = LECCIONES_POR_TEMA + 2 // + aplicado + boss
    const val EJERCICIOS_POR_SESION = 5

    private const val BOSS_SEED_INICIO = 33
    private const val BOSS_FINAL_SEED_INICIO = 50

    data class Tema(
        val id: Int,
        val nombre: String,
        val area: String,
        val bossNombre: String,
        val subtemas: List<String>
    )

    val TEMAS: List<Tema> = listOf(
        Tema(
            id = 1,
            nombre = "Fundamentos de Álgebra",
            area = "Álgebra",
            bossNombre = "Guardián de las Ecuaciones",
            subtemas = listOf(
                "Ecuaciones con suma",
                "Ecuaciones con resta",
                "Coeficientes y constantes",
                "Paréntesis y distribución"
            )
        ),
        Tema(
            id = 2,
            nombre = "Ecuaciones Cuadráticas",
            area = "Álgebra",
            bossNombre = "Serpiente Cuadrática",
            subtemas = listOf(
                "Factorización",
                "Diferencia de cuadrados",
                "Raíces de la ecuación",
                "Fórmula general"
            )
        ),
        Tema(
            id = 3,
            nombre = "Introducción al Cálculo",
            area = "Cálculo",
            bossNombre = "Titán de los Límites",
            subtemas = listOf(
                "Límites básicos",
                "Regla de la potencia",
                "Derivadas de polinomios",
                "Límites cuadráticos"
            )
        ),
        Tema(
            id = 4,
            nombre = "Factorización y Fracciones Algebraicas",
            area = "Álgebra",
            bossNombre = "Gólem de las Fracciones",
            subtemas = listOf(
                "Factor común",
                "Diferencia de cuadrados",
                "Trinomios",
                "Fracciones algebraicas"
            )
        ),
        Tema(
            id = 5,
            nombre = "Desigualdades y Números Complejos",
            area = "Álgebra",
            bossNombre = "Espectro Complejo",
            subtemas = listOf(
                "Desigualdades lineales",
                "Desigualdades cuadráticas",
                "Suma de complejos",
                "Producto de complejos"
            )
        ),
        Tema(
            id = 6,
            nombre = "Geometría Plana y Pitágoras",
            area = "Geometría",
            bossNombre = "Gigante de la Geometría",
            subtemas = listOf(
                "Áreas",
                "Perímetros",
                "Teorema de Pitágoras",
                "Distancia entre puntos"
            )
        )
    )

    fun nombreTema(temaId: Int): String =
        TEMAS.find { it.id == temaId }?.nombre ?: "Batalla Final"

    fun generarNodos(): List<NodoCamino> {
        val nodos = mutableListOf<NodoCamino>()
        var id = 1
        TEMAS.forEach { tema ->
            repeat(LECCIONES_POR_TEMA) { l ->
                nodos += crearNodo(
                    id = id,
                    titulo = "Lección ${l + 1}: ${tema.subtemas[l]}",
                    tema = tema,
                    tipo = TIPO_LECCION
                )
                id++
            }
            nodos += crearNodo(
                id = id,
                titulo = "Desafío Aplicado",
                tema = tema,
                tipo = TIPO_APLICADO,
                descripcion = "Problemas del mundo real: ${tema.nombre}"
            )
            id++
            nodos += crearNodo(
                id = id,
                titulo = "Jefe: ${tema.bossNombre}",
                tema = tema,
                tipo = TIPO_BOSS,
                descripcion = "Derrota al jefe para dominar ${tema.nombre}"
            )
            id++
        }
        nodos += NodoCamino(
            id = id,
            titulo = "JEFE FINAL: El Dragón del Caos",
            descripcion = "Pon a prueba todo lo aprendido",
            areaMatematica = "Todos",
            nivelOrden = id,
            estaDesbloqueado = false,
            estaCompletado = false,
            nodoPrerrequisitoId = id - 1,
            tipo = TIPO_BOSS_FINAL,
            temaId = 0
        )
        return nodos
    }

    private fun crearNodo(
        id: Int,
        titulo: String,
        tema: Tema,
        tipo: String,
        descripcion: String = tema.nombre
    ) = NodoCamino(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        areaMatematica = tema.area,
        nivelOrden = id,
        estaDesbloqueado = id == 1,
        estaCompletado = false,
        nodoPrerrequisitoId = if (id == 1) null else id - 1,
        tipo = tipo,
        temaId = tema.id
    )

    fun generarEjercicios(): List<Ejercicio> {
        val ejercicios = mutableListOf<Ejercicio>()
        generarNodos().forEach { nodo ->
            ejercicios += when (nodo.tipo) {
                TIPO_LECCION -> ejerciciosLeccion(nodo)
                TIPO_APLICADO -> ejerciciosAplicados(nodo)
                TIPO_BOSS -> ejerciciosBoss(nodo)
                else -> ejerciciosBossFinal(nodo)
            }
        }
        return ejercicios
    }

    // Cada lección tiene un pool de 8 ejercicios; la sesión toma 5 al azar
    private fun ejerciciosLeccion(nodo: NodoCamino): List<Ejercicio> {
        val posicionEnTema = (nodo.nivelOrden - 1) % NODOS_POR_TEMA // 0..3 para lecciones
        val inicio = posicionEnTema * EJERCICIOS_POR_LECCION + 1
        val dificultad = posicionEnTema + 1
        return (inicio until inicio + EJERCICIOS_POR_LECCION).map { d ->
            generadorTema(nodo.temaId, d, nodo.id, EJ_NORMAL, dificultad)
        }
    }

    private fun ejerciciosAplicados(nodo: NodoCamino): List<Ejercicio> =
        (1..APLICADOS_POR_TEMA).map { d -> generadorAplicado(nodo.temaId, d, nodo.id) }

    private fun ejerciciosBoss(nodo: NodoCamino): List<Ejercicio> =
        (1..BOSS_EJERCICIOS).map { i ->
            generadorTema(nodo.temaId, BOSS_SEED_INICIO + i, nodo.id, EJ_BOSS, 3)
        }

    private fun ejerciciosBossFinal(nodo: NodoCamino): List<Ejercicio> =
        (1..BOSS_FINAL_EJERCICIOS).map { i ->
            val temaId = (i - 1) % TEMAS.size + 1
            generadorTema(temaId, BOSS_FINAL_SEED_INICIO + i, nodo.id, EJ_BOSS, 3)
        }

    private fun generadorTema(
        temaId: Int,
        d: Int,
        nodoId: Int,
        tipo: String,
        dificultad: Int
    ): Ejercicio = when (temaId) {
        1 -> algebraLineal(d, nodoId, tipo, dificultad)
        2 -> cuadraticas(d, nodoId, tipo, dificultad)
        3 -> calculo(d, nodoId, tipo, dificultad)
        4 -> factorizacion(d, nodoId, tipo, dificultad)
        5 -> complejosYDesigualdades(d, nodoId, tipo, dificultad)
        6 -> geometria(d, nodoId, tipo, dificultad)
        else -> throw IllegalArgumentException("Tema no soportado: $temaId")
    }

    private fun generadorAplicado(temaId: Int, d: Int, nodoId: Int): Ejercicio = when (temaId) {
        1 -> aplicadoAlgebra(d, nodoId)
        2 -> aplicadoCuadraticas(d, nodoId)
        3 -> aplicadoCalculo(d, nodoId)
        4 -> aplicadoFactorizacion(d, nodoId)
        5 -> aplicadoComplejos(d, nodoId)
        6 -> aplicadoGeometria(d, nodoId)
        else -> throw IllegalArgumentException("Tema no soportado: $temaId")
    }

    private fun crearEjercicio(
        id: Int,
        nodoId: Int,
        enunciado: String,
        respuestaCorrecta: String,
        distractores: List<String>,
        explicacion: String,
        dificultad: Int,
        tipo: String = EJ_NORMAL
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
            explicacion = explicacion,
            tipo = tipo
        )
    }

    // ===================== TEMA 1: Fundamentos de Álgebra =====================
    private fun algebraLineal(d: Int, nodoId: Int, tipo: String, dificultad: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (((d - 1) / 2) % 4) {
            0 -> {
                val a = (d % 4) + 2
                val x = d
                val b = d + 5
                val c = a * x + b
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: ${a}x + $b = $c",
                    "x = $x",
                    listOf("x = ${x + 1}", "x = ${x - 1}", "x = ${x + 2}"),
                    "Restamos $b y dividimos entre $a: x = ($c - $b) / $a = $x.",
                    dificultad, tipo
                )
            }
            1 -> {
                val a = (d % 3) + 2
                val x = d
                val b = d * 2
                val c = a * x - b
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: ${a}x - $b = $c",
                    "x = $x",
                    listOf("x = ${x + 1}", "x = ${x - 2}", "x = ${x + 3}"),
                    "Sumamos $b y dividimos entre $a: x = ($c + $b) / $a = $x.",
                    dificultad, tipo
                )
            }
            2 -> {
                val x = d + 3
                val numerador = d * 2
                val c = x + d
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: (2x + $numerador) / 2 = $c",
                    "x = $x",
                    listOf("x = ${x + 1}", "x = ${x - 1}", "x = ${c}"),
                    "Multiplicamos por 2: 2x + $numerador = ${c * 2}. Restamos $numerador: 2x = ${c * 2 - numerador}. Dividimos entre 2: x = $x.",
                    dificultad, tipo
                )
            }
            else -> {
                val a = (d % 4) + 2
                val p = (d % 5) + 1
                val x = d
                val q = a * (x + p)
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: $a(x + $p) = $q",
                    "x = $x",
                    listOf("x = ${x + p}", "x = ${x - 1}", "x = ${x + 1}"),
                    "Dividimos entre $a: x + $p = ${q / a}. Restamos $p: x = $x.",
                    dificultad, tipo
                )
            }
        }
    }

    // ===================== TEMA 2: Ecuaciones Cuadráticas =====================
    private fun cuadraticas(d: Int, nodoId: Int, tipo: String, dificultad: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (((d - 1) / 2) % 4) {
            0 -> {
                val r = (d % 5) + 2
                val s = r + (d % 3) + 1
                val b = r + s
                val c = r * s
                crearEjercicio(
                    id, nodoId,
                    "Factoriza: x² - ${b}x + $c = 0",
                    "(x - $r)(x - $s)",
                    listOf("(x + $r)(x - $s)", "(x - ${r + 1})(x - $s)", "(x + $r)(x + $s)"),
                    "Buscamos números que sumen $b y multipliquen $c: $r y $s.",
                    dificultad, tipo
                )
            }
            1 -> {
                val n = (d % 6) + 3
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: x² - ${n * n} = 0",
                    "x = ±$n",
                    listOf("x = $n", "x = -$n", "x = ±${n + 1}"),
                    "x² = ${n * n}, por lo que x puede ser $n o -$n.",
                    dificultad, tipo
                )
            }
            2 -> {
                val r = (d % 4) + 2
                val s = r + (d % 2) + 2
                val b = r + s
                val c = r * s
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: x² - ${b}x + $c = 0",
                    "x = $r, x = $s",
                    listOf("x = ${r + 1}, x = $s", "x = -$r, x = -$s", "x = $r, x = ${s + 1}"),
                    "Factorizamos (x - $r)(x - $s) = 0, por lo que x = $r o x = $s.",
                    dificultad, tipo
                )
            }
            else -> {
                val r = (d % 4) + 1
                val s = r + 2
                val b = 2 * (r + s)
                val c = 2 * r * s
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: 2x² - ${b}x + $c = 0 (usa fórmula general)",
                    "x = $r, x = $s",
                    listOf("x = ${r + 1}, x = ${s + 1}", "x = -$r, x = -$s", "x = $b, x = $c"),
                    "El discriminante es ${b * b - 8 * c}. Aplicando la fórmula general: x = $r o x = $s.",
                    dificultad, tipo
                )
            }
        }
    }

    // ===================== TEMA 3: Introducción al Cálculo =====================
    private fun calculo(d: Int, nodoId: Int, tipo: String, dificultad: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (((d - 1) / 2) % 4) {
            0 -> {
                val m = (d % 4) + 2
                val b = d + 2
                val x = (d % 5) + 1
                crearEjercicio(
                    id, nodoId,
                    "Límite de ${m}x + $b cuando x tiende a $x",
                    "${m * x + b}",
                    listOf("${m * x}", "$b", "${m * x + b + 1}"),
                    "Sustituimos x = $x: $m($x) + $b = ${m * x + b}.",
                    dificultad, tipo
                )
            }
            1 -> {
                val n = (d % 6) + 3
                crearEjercicio(
                    id, nodoId,
                    "Derivada de f(x) = x^$n",
                    "${n}x^${n - 1}",
                    listOf("x^${n - 1}", "${n - 1}x^$n", "x^$n"),
                    "Aplicamos d/dx(xⁿ) = n·xⁿ⁻¹: ${n}x^${n - 1}.",
                    dificultad, tipo
                )
            }
            2 -> {
                val a = (d % 5) + 2
                val b = (d % 7) + 2
                crearEjercicio(
                    id, nodoId,
                    "Derivada de f(x) = ${a}x² + ${b}x",
                    "${2 * a}x + $b",
                    listOf("${a}x + $b", "${2 * a}x", "$b"),
                    "Derivamos término a término: ${2 * a}x + $b.",
                    dificultad, tipo
                )
            }
            else -> {
                val a = d + 4
                val b = (d % 4) + 2
                crearEjercicio(
                    id, nodoId,
                    "Límite cuando x tiende a $b de (x² - $a)",
                    "${b * b - a}",
                    listOf("${b * b}", "${a - b * b}", "0"),
                    "Sustituimos x = $b: $b² - $a = ${b * b - a}.",
                    dificultad, tipo
                )
            }
        }
    }

    // ===================== TEMA 4: Factorización =====================
    private fun factorizacion(d: Int, nodoId: Int, tipo: String, dificultad: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (((d - 1) / 2) % 4) {
            0 -> {
                val a = (d % 4) + 2
                val k = d + 2
                crearEjercicio(
                    id, nodoId,
                    "Factoriza: ${a}x² + ${a * k}x",
                    "${a}x(x + $k)",
                    listOf("x(x + $k)", "${a}(x + $k)", "x² + ${k}x"),
                    "Sacamos el factor común ${a}x: ${a}x(x + $k).",
                    dificultad, tipo
                )
            }
            1 -> {
                val n = (d % 6) + 3
                crearEjercicio(
                    id, nodoId,
                    "Factoriza: x² - ${n * n}",
                    "(x - $n)(x + $n)",
                    listOf("(x - $n)²", "(x + $n)²", "x(x - $n)"),
                    "Diferencia de cuadrados: (x - $n)(x + $n).",
                    dificultad, tipo
                )
            }
            2 -> {
                val a = (d % 5) + 2
                val b = (d % 4) + 3
                crearEjercicio(
                    id, nodoId,
                    "Factoriza: x² + ${a + b}x + ${a * b}",
                    "(x + $a)(x + $b)",
                    listOf("(x - $a)(x - $b)", "(x + ${a + 1})(x + $b)", "(x + $a)(x - $b)"),
                    "Buscamos números que sumen ${a + b} y multipliquen ${a * b}: $a y $b.",
                    dificultad, tipo
                )
            }
            else -> {
                val n = (d % 5) + 4
                crearEjercicio(
                    id, nodoId,
                    "Simplifica: (x² - ${n * n}) / (x - $n)",
                    "x + $n",
                    listOf("x - $n", "x² - $n", "1"),
                    "x² - ${n * n} = (x - $n)(x + $n). Cancelamos (x - $n).",
                    dificultad, tipo
                )
            }
        }
    }

    // ===================== TEMA 5: Desigualdades y Complejos =====================
    private fun complejosYDesigualdades(d: Int, nodoId: Int, tipo: String, dificultad: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (((d - 1) / 2) % 4) {
            0 -> {
                val s = (d % 6) + 2
                val a = (d % 3) + 2
                val b = d + 3
                val c = a * s - b
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: ${a}x - $b > $c",
                    "x > $s",
                    listOf("x < $s", "x ≥ $s", "x > ${s + 1}"),
                    "Sumamos $b: ${a}x > ${c + b}. Dividimos entre $a: x > $s.",
                    dificultad, tipo
                )
            }
            1 -> {
                val p = (d % 4) + 1
                val q = p + (d % 3) + 2
                crearEjercicio(
                    id, nodoId,
                    "Resuelve: x² - ${p + q}x + ${p * q} ≤ 0",
                    "$p ≤ x ≤ $q",
                    listOf("x ≤ $p", "x ≥ $q", "$q ≤ x ≤ $p"),
                    "x² - ${p + q}x + ${p * q} = (x - $p)(x - $q). La solución es $p ≤ x ≤ $q.",
                    dificultad, tipo
                )
            }
            2 -> {
                val real = (d % 5) + 2
                val img = (d % 4) + 1
                crearEjercicio(
                    id, nodoId,
                    "Si z = $real + ${img}i y w = 2 + 3i, encuentra z + w",
                    "${real + 2} + ${img + 3}i",
                    listOf("${real + 2} + ${img}i", "$real + ${img + 3}i", "${real + img} + ${real + 2}i"),
                    "Sumamos parte real e imaginaria: ($real + 2) + ($img + 3)i.",
                    dificultad, tipo
                )
            }
            else -> {
                val a = (d % 4) + 1
                val b = (d % 3) + 2
                val real = a * 3 - 2 * b
                val img = a * b + 6
                crearEjercicio(
                    id, nodoId,
                    "Multiplica: ($a + 2i)(3 + ${b}i)",
                    "$real + ${img}i",
                    listOf("${a * 3} + ${img}i", "$real + ${a * b}i", "${a + 3} + ${2 + b}i"),
                    "Usamos (a+bi)(c+di) = (ac - bd) + (ad + bc)i.",
                    dificultad, tipo
                )
            }
        }
    }

    // ===================== TEMA 6: Geometría =====================
    private fun geometria(d: Int, nodoId: Int, tipo: String, dificultad: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when (((d - 1) / 2) % 4) {
            0 -> {
                val base = (d % 6) + 3
                val altura = (d % 5) + 2
                crearEjercicio(
                    id, nodoId,
                    "Calcula el área de un rectángulo de base $base y altura $altura",
                    "${base * altura}",
                    listOf("${base + altura}", "${2 * (base + altura)}", "${base * altura + 1}"),
                    "Área = base × altura = $base × $altura = ${base * altura}.",
                    dificultad, tipo
                )
            }
            1 -> {
                val a = (d % 5) + 3
                val b = (d % 4) + 2
                crearEjercicio(
                    id, nodoId,
                    "Perímetro de un rectángulo de lados $a y $b",
                    "${2 * (a + b)}",
                    listOf("${a + b}", "${a * b}", "${2 * a + b}"),
                    "Perímetro = 2(a + b) = 2($a + $b) = ${2 * (a + b)}.",
                    dificultad, tipo
                )
            }
            2 -> {
                val a = (d % 4) + 3
                val b = (d % 5) + 4
                val hip = a * a + b * b
                crearEjercicio(
                    id, nodoId,
                    "Halla la hipotenusa de un triángulo rectángulo con catetos $a y $b",
                    "√$hip",
                    listOf("$hip", "√${hip + 1}", "${a + b}"),
                    "Por Pitágoras: c² = $a² + $b² = $hip, entonces c = √$hip.",
                    dificultad, tipo
                )
            }
            else -> {
                val x2 = (d % 5) + 3
                val y2 = (d % 4) + 2
                val dist = x2 * x2 + y2 * y2
                crearEjercicio(
                    id, nodoId,
                    "Distancia entre los puntos (0, 0) y ($x2, $y2)",
                    "√$dist",
                    listOf("$dist", "√${dist + 1}", "${x2 + y2}"),
                    "d = √[(x2 - x1)² + (y2 - y1)²] = √($x2² + $y2²) = √$dist.",
                    dificultad, tipo
                )
            }
        }
    }

    // ===================== PROBLEMAS APLICADOS =====================

    private fun aplicadoAlgebra(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when ((d - 1) % 4) {
            0 -> {
                val a = (d % 4) + 2
                val x = d + 2
                val b = (d % 5) + 5
                val c = a * x + b
                crearEjercicio(
                    id, nodoId,
                    "Ana compró $a cuadernos iguales y una mochila de Q$b. Pagó Q$c en total. ¿Cuánto cuesta cada cuaderno?",
                    "Q$x",
                    listOf("Q${x + 1}", "Q${x - 1}", "Q${x + 2}"),
                    "Planteamos ${a}x + $b = $c. Restamos $b y dividimos entre $a: x = $x.",
                    2, EJ_APLICADO
                )
            }
            1 -> {
                val a = (d % 3) + 2
                val x = d + 1
                val b = d + 4
                val c = a * x + b
                crearEjercicio(
                    id, nodoId,
                    "Un número multiplicado por $a y aumentado en $b es igual a $c. ¿Cuál es el número?",
                    "$x",
                    listOf("${x + 1}", "${x - 1}", "${x + 3}"),
                    "Planteamos ${a}x + $b = $c. Entonces x = ($c - $b) / $a = $x.",
                    2, EJ_APLICADO
                )
            }
            2 -> {
                val x = d + 3
                val k = 2
                val s = 2 * x + k
                crearEjercicio(
                    id, nodoId,
                    "Juan y Pedro tienen $s canicas entre los dos. Juan tiene $k más que Pedro. ¿Cuántas tiene Pedro?",
                    "$x",
                    listOf("${x + k}", "${x + 1}", "${x - 1}"),
                    "Si Pedro tiene x, Juan tiene x + $k: x + (x + $k) = $s → 2x = ${s - k} → x = $x.",
                    2, EJ_APLICADO
                )
            }
            else -> {
                val x = d + 4
                val s = 3 * x + 3
                crearEjercicio(
                    id, nodoId,
                    "La suma de tres enteros consecutivos es $s. ¿Cuál es el número menor?",
                    "$x",
                    listOf("${x + 1}", "${x + 2}", "${x - 1}"),
                    "Sea x el menor: x + (x+1) + (x+2) = $s → 3x + 3 = $s → x = $x.",
                    2, EJ_APLICADO
                )
            }
        }
    }

    private fun aplicadoCuadraticas(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when ((d - 1) % 4) {
            0 -> {
                val n = d + 3
                val p = n * (n + 1)
                crearEjercicio(
                    id, nodoId,
                    "El producto de dos enteros positivos consecutivos es $p. ¿Cuál es el número menor?",
                    "$n",
                    listOf("${n + 1}", "${n - 1}", "${n + 2}"),
                    "Planteamos x(x + 1) = $p → x² + x - $p = 0. Factorizando: x = $n.",
                    3, EJ_APLICADO
                )
            }
            1 -> {
                val w = d + 3
                val k = (d % 3) + 1
                val area = w * (w + k)
                crearEjercicio(
                    id, nodoId,
                    "Un terreno rectangular tiene un área de $area m² y su largo mide $k m más que su ancho. ¿Cuánto mide el ancho?",
                    "$w m",
                    listOf("${w + k} m", "${w + 1} m", "${w - 1} m"),
                    "x(x + $k) = $area → x² + ${k}x - $area = 0. La solución positiva es x = $w.",
                    3, EJ_APLICADO
                )
            }
            2 -> {
                val r = (d % 3) + 1
                val s = r + (d % 2) + 2
                val v = r + s
                val h = r * s
                crearEjercicio(
                    id, nodoId,
                    "Una pelota lanzada sigue la trayectoria h(t) = -t² + ${v}t metros. ¿En qué tiempos alcanza una altura de $h m?",
                    "t = $r y t = $s",
                    listOf("t = ${r + 1} y t = $s", "t = $r y t = ${s + 1}", "t = $v"),
                    "Resolvemos -t² + ${v}t = $h → t² - ${v}t + $h = 0 → (t - $r)(t - $s) = 0.",
                    3, EJ_APLICADO
                )
            }
            else -> {
                val x = d + 5
                val k = (d % 3) + 2
                val c = x * x - k * x
                crearEjercicio(
                    id, nodoId,
                    "El cuadrado de un número menos $k veces el número es igual a $c. ¿Cuál es el número positivo?",
                    "$x",
                    listOf("$k", "${x - 1}", "${x + 1}"),
                    "x² - ${k}x = $c → x² - ${k}x - $c = 0. La solución positiva es x = $x.",
                    3, EJ_APLICADO
                )
            }
        }
    }

    private fun aplicadoCalculo(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when ((d - 1) % 4) {
            0 -> {
                val m = (d % 4) + 2
                val b = d + 5
                val x = (d % 4) + 2
                crearEjercicio(
                    id, nodoId,
                    "Un auto acelera según v(t) = ${m}t + $b km/h. ¿Qué velocidad alcanza a los $x segundos?",
                    "${m * x + b} km/h",
                    listOf("${m * x} km/h", "$b km/h", "${m * x + b + 2} km/h"),
                    "Evaluamos v($x) = $m($x) + $b = ${m * x + b} km/h.",
                    2, EJ_APLICADO
                )
            }
            1 -> {
                val a = (d % 4) + 3
                val c = d + 10
                crearEjercicio(
                    id, nodoId,
                    "La temperatura de un horno es T(t) = t² + ${a}t + $c °C. ¿Cuál es la razón de cambio T'(t)?",
                    "2t + $a",
                    listOf("t + $a", "2t + $c", "t² + $a"),
                    "Derivamos término a término: d/dt(t²) = 2t, d/dt(${a}t) = $a, la constante $c desaparece.",
                    2, EJ_APLICADO
                )
            }
            2 -> {
                val a = (d % 3) + 2
                val b = d + 8
                crearEjercicio(
                    id, nodoId,
                    "El costo de producir x artículos es C(x) = ${a}x² + ${b}x quetzales. ¿Cuál es el costo marginal C'(x)?",
                    "${2 * a}x + $b",
                    listOf("${a}x + $b", "${2 * a}x", "${a}x² + ${b}"),
                    "El costo marginal es la derivada: C'(x) = ${2 * a}x + $b.",
                    2, EJ_APLICADO
                )
            }
            else -> {
                val a = (d % 3) + 1
                crearEjercicio(
                    id, nodoId,
                    "Una población de bacterias crece según P(t) = ${a}t³. ¿Cuál es su tasa de crecimiento instantánea P'(t)?",
                    "${3 * a}t²",
                    listOf("${a}t²", "3t²", "${3 * a}t"),
                    "Por la regla de la potencia: P'(t) = $a · 3t² = ${3 * a}t².",
                    2, EJ_APLICADO
                )
            }
        }
    }

    private fun aplicadoFactorizacion(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when ((d - 1) % 4) {
            0 -> {
                val a = (d % 3) + 2
                val k = d + 2
                crearEjercicio(
                    id, nodoId,
                    "Un lote tiene área ${a}x² + ${a * k}x m² y ancho ${a}x m. ¿Cuál es la expresión factorizada de su largo?",
                    "x + $k",
                    listOf("x - $k", "${a}x + $k", "x + ${a * k}"),
                    "${a}x² + ${a * k}x = ${a}x(x + $k). Si el ancho es ${a}x, el largo es x + $k.",
                    3, EJ_APLICADO
                )
            }
            1 -> {
                val n = (d % 5) + 3
                crearEjercicio(
                    id, nodoId,
                    "Un marco se diseña recortando un cuadrado de lado $n de uno de lado x. El área restante es x² - ${n * n}. ¿Cómo se factoriza?",
                    "(x - $n)(x + $n)",
                    listOf("(x - $n)²", "(x + $n)²", "x(x - $n)"),
                    "Es una diferencia de cuadrados: x² - ${n * n} = (x - $n)(x + $n).",
                    3, EJ_APLICADO
                )
            }
            2 -> {
                val a = (d % 4) + 2
                val b = (d % 3) + 3
                crearEjercicio(
                    id, nodoId,
                    "El área de un salón rectangular es x² + ${a + b}x + ${a * b} m². Si sus lados son binomios, ¿cuáles son?",
                    "(x + $a) y (x + $b)",
                    listOf("(x - $a) y (x - $b)", "(x + $a) y (x - $b)", "(x + ${a + b}) y (x + ${a * b})"),
                    "Factorizamos x² + ${a + b}x + ${a * b} = (x + $a)(x + $b).",
                    3, EJ_APLICADO
                )
            }
            else -> {
                val n = (d % 4) + 3
                crearEjercicio(
                    id, nodoId,
                    "En una receta, la proporción de ingredientes es (x² - ${n * n}) / (x - $n). ¿Cuál es la forma simplificada?",
                    "x + $n",
                    listOf("x - $n", "x² + $n", "$n"),
                    "Factorizamos el numerador: (x - $n)(x + $n) / (x - $n) = x + $n.",
                    3, EJ_APLICADO
                )
            }
        }
    }

    private fun aplicadoComplejos(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when ((d - 1) % 4) {
            0 -> {
                val a = (d % 3) + 2
                val x = (d % 4) + 2
                val b = d + 4
                val c = a * x + b
                crearEjercicio(
                    id, nodoId,
                    "Luis puede gastar máximo Q$c en el cine. Cada entrada cuesta Q$a y ya gastó Q$b en snacks. ¿Cuántas entradas x puede comprar?",
                    "x ≤ $x",
                    listOf("x ≥ $x", "x < ${x + 1}", "x ≤ ${x + 1}"),
                    "Planteamos ${a}x + $b ≤ $c → ${a}x ≤ ${c - b} → x ≤ $x.",
                    3, EJ_APLICADO
                )
            }
            1 -> {
                val p = (d % 4) + 1
                val q = p + (d % 3) + 2
                crearEjercicio(
                    id, nodoId,
                    "Un puente soporta un peso x (toneladas) solo si cumple x² - ${p + q}x + ${p * q} ≤ 0. ¿Qué rango de peso es seguro?",
                    "$p ≤ x ≤ $q",
                    listOf("x ≤ $p", "x ≥ $q", "$q ≤ x ≤ $p"),
                    "Factorizamos (x - $p)(x - $q) ≤ 0. El rango seguro es $p ≤ x ≤ $q.",
                    3, EJ_APLICADO
                )
            }
            2 -> {
                val r = (d % 5) + 2
                val i = (d % 4) + 1
                crearEjercicio(
                    id, nodoId,
                    "En un circuito, dos impedancias en serie son Z₁ = $r + ${i}i Ω y Z₂ = 2 + 3i Ω. ¿Cuál es la impedancia total?",
                    "${r + 2} + ${i + 3}i Ω",
                    listOf("${r + 2} + ${i}i Ω", "$r + ${i + 3}i Ω", "${r + i} + ${r + 2}i Ω"),
                    "En serie se suman: ($r + 2) + ($i + 3)i = ${r + 2} + ${i + 3}i Ω.",
                    3, EJ_APLICADO
                )
            }
            else -> {
                val a = (d % 4) + 1
                val b = (d % 3) + 2
                val real = a * 3 - 2 * b
                val img = a * b + 6
                crearEjercicio(
                    id, nodoId,
                    "La potencia en un sistema se modela con el producto ($a + 2i)(3 + ${b}i). ¿Cuál es el resultado?",
                    "$real + ${img}i",
                    listOf("${a * 3} + ${img}i", "$real + ${a * b}i", "${a + 3} + ${2 + b}i"),
                    "Aplicamos (a+bi)(c+di) = (ac - bd) + (ad + bc)i = $real + ${img}i.",
                    3, EJ_APLICADO
                )
            }
        }
    }

    private fun aplicadoGeometria(d: Int, nodoId: Int): Ejercicio {
        val id = nodoId * 1000 + d
        return when ((d - 1) % 4) {
            0 -> {
                val a = (d % 4) + 3
                val b = (d % 3) + 4
                val hip = a * a + b * b
                crearEjercicio(
                    id, nodoId,
                    "Una escalera se apoya en una pared formando un triángulo rectángulo con base $a m y altura $b m. ¿Cuánto mide la escalera?",
                    "√$hip m",
                    listOf("$hip m", "√${hip + 1} m", "${a + b} m"),
                    "La escalera es la hipotenusa: c = √($a² + $b²) = √$hip m.",
                    2, EJ_APLICADO
                )
            }
            1 -> {
                val a = (d % 5) + 3
                val b = (d % 4) + 2
                crearEjercicio(
                    id, nodoId,
                    "Se quiere cercar un jardín rectangular de $a m × $b m. ¿Cuántos metros de cerca se necesitan?",
                    "${2 * (a + b)} m",
                    listOf("${a + b} m", "${a * b} m", "${2 * a + b} m"),
                    "Necesitamos el perímetro: 2($a + $b) = ${2 * (a + b)} m.",
                    2, EJ_APLICADO
                )
            }
            2 -> {
                val base = (d % 6) + 3
                val altura = (d % 5) + 2
                crearEjercicio(
                    id, nodoId,
                    "Una ventana rectangular mide $base cm de base y $altura cm de alto. ¿Cuál es el área del vidrio?",
                    "${base * altura} cm²",
                    listOf("${base + altura} cm²", "${2 * (base + altura)} cm²", "${base * altura + 2} cm²"),
                    "Área = base × altura = $base × $altura = ${base * altura} cm².",
                    2, EJ_APLICADO
                )
            }
            else -> {
                val x = (d % 5) + 3
                val y = (d % 4) + 2
                val dist = x * x + y * y
                crearEjercicio(
                    id, nodoId,
                    "Un dron despega del punto (0, 0) y aterriza en el punto ($x, $y). ¿Qué distancia recorrió?",
                    "√$dist",
                    listOf("$dist", "√${dist + 1}", "${x + y}"),
                    "La distancia es d = √($x² + $y²) = √$dist.",
                    2, EJ_APLICADO
                )
            }
        }
    }
}
