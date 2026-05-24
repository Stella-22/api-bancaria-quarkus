# Analise de Cobertura de Testes

Projeto: `api-bancaria-quarkus`

Relatorio gerado com JaCoCo apos execucao do comando:

```bash
mvn clean verify
```

Arquivo HTML do relatorio:

```text
target/site/jacoco/index.html
```

Fonte dos dados consolidados:

```text
target/site/jacoco/jacoco.csv
```

## Resumo Geral

| Metrica | Cobertura | Coberto / Total |
|---|---:|---:|
| Instrucoes | 84,81% | 1262 / 1488 |
| Linhas | 82,74% | 326 / 394 |
| Metodos | 88,59% | 132 / 149 |
| Branches | 63,10% | 53 / 84 |

O projeto atingiu a meta mínima de cobertura geral de linhas e branches (70% linha / 60% branch). 

## Cobertura Por Pacote

| Pacote | Cobertura de Linhas | Cobertura de Branches | Linhas Cobertas |
|---|---:|---:|---:|
| `br.com.ada.estela.enums` | 100,00% | N/A | 12 / 12 |
| `br.com.ada.estela.exception` | 100,00% | N/A | 2 / 2 |
| `br.com.ada.estela.mappers` | 50,00% | 50,00% | 33 / 66 |
| `br.com.ada.estela.model` | 73,44% | N/A | 47 / 64 |
| `br.com.ada.estela.repository` | 100,00% | N/A | 4 / 4 |
| `br.com.ada.estela.resource` | 100,00% | N/A | 1 / 1 |
| `br.com.ada.estela.resource.auth` | 100,00% | 100,00% | 7 / 7 |
| `br.com.ada.estela.resource.cliente` | 100,00% | 100,00% | 33 / 33 |
| `br.com.ada.estela.resource.conta` | 92,31% | 100,00% | 48 / 52 |
| `br.com.ada.estela.resource.transacao` | 100,00% | 100,00% | 28 / 28 |
| `br.com.ada.estela.service` | 88,80% | 62,07% | 111 / 125 |

## Testes Considerados

Foram considerados testes unitarios e testes de integracao executados pelo Maven/Surefire:

| Classe de teste | Tipo |
|---|---|
| `AuthServiceTest` | Unitario |
| `ClienteServiceTest` | Unitario |
| `ContaServiceTest` | Unitario |
| `TransacaoServiceTest` | Unitario |
| `AuthResourceTest` | Integracao |
| `ClienteResourceTest` | Integracao |
| `ContaResourceTest` | Integracao |
| `TransacaoResourceTest` | Integracao |

## Pontos Fortes

- As classes de resource ficaram bem cobertas, com destaque para `AuthResource`, `ClienteResource` e `TransacaoResource`, que chegaram a 100% de linhas e branches.
- Os principais fluxos HTTP foram exercitados com `RestAssured`, incluindo status codes de sucesso e erro.
- Os testes de integracao validam tambem o corpo das respostas JSON em cenarios relevantes.
- Os servicos possuem boa cobertura de linhas, especialmente considerando as regras de negocio de cadastro, busca, autenticacao, conta e transacoes.

## Conclusao

A cobertura geral do projeto ficou adequada, com 82,74% de linhas cobertas e 88,59% de metodos cobertos. Os endpoints principais da API bancaria foram testados por integracao com Quarkus e RestAssured, enquanto as regras de negocio foram complementadas por testes unitarios de servico.
