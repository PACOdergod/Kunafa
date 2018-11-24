package com.narbase.kunafa.core.css


class Stylesheet

@Suppress("MemberVisibilityCanBePrivate")
class RuleSet(val selector: Selector, val atRule: String? = null) {

    val rules: MutableSet<Rule<*>> = mutableSetOf()

    var subRuleSets: MutableSet<RuleSet>? = null

    var atRuleSets: MutableSet<RuleSet>? = null

    fun <T> getProperty(name: String): T? {
        val list = rules.filterIsInstance<Rule<T>>()
        return list.find { it.name == name }?.value
    }

    fun <T> setProperty(name: String, value: T) {
        rules.add(Rule(name, value))
    }

    fun toRulesList(): List<RuleSet> {
        val list = mutableListOf(this)
        if (atRule != null) return list
        subRuleSets?.let {
            it.forEach { set ->
                list.addAll(set.toRulesList())
            }
        }
        atRuleSets?.let {
            it.forEach { set ->
                list.addAll(set.toRulesList())
            }
        }
        return list
    }

    override fun toString(): String {
        return buildString {

            atRule?.let {
                append(atRule)
                append('{')
            }
            append(selector.toString())
            append("{")
            rules.forEachIndexed { index, rule ->
                if (index != 0)
                    append(',')
                append(rule.toString())
            }
            append('}')

            atRule?.let {
                subRuleSets?.forEach { subRuleSet ->
                    append(' ')
                    append(subRuleSet)
                }
                append('}')
            }
        }
    }

    fun addPseudo(name: String, rules: RuleSet.() -> Unit): RuleSet {
        if (subRuleSets == null) subRuleSets = mutableSetOf()
        val set = RuleSet(PseudoSelector(selector, name)).apply(rules)
        subRuleSets?.add(set)
        return set
    }

    fun addAtRule(name: String, rules: RuleSet.() -> Unit): RuleSet {
        if (atRuleSets == null) atRuleSets = mutableSetOf()
        val set = RuleSet(selector, atRule = "@$name").apply(rules)
        atRuleSets?.add(set)
        return set
    }
}