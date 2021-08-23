package com.digimon.investment.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@ComponentScan(basePackages = ["com.digimon.investment.**"])
@SpringBootApplication
class InvestmentApplication

fun main(args: Array<String>) {
	runApplication<InvestmentApplication>(*args)
}
