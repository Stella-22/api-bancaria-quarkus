📋 README - API Bancária Quarkus

📌 Título e Descrição

# API Bancária Quarkus

Uma API RESTful robusta desenvolvida com Quarkus para gerenciar operações bancárias completas.
O projeto oferece funcionalidades de autenticação segura com JWT, gerenciamento de clientes, contas bancárias e transações (depósitos, saques e transferências). 
Construída para demonstrar boas práticas de desenvolvimento em JAVA moderno com foco em performance, segurança e escalabilidade.


💻 Instalação
# Pré-requisitos
Antes de começar, certifique-se de que possui os seguintes itens instalados:
Java 25+ (JDK)
Maven 3.8+
Docker e Docker Compose (para o banco de dados PostgreSQL)
Git

#Passo a Passo
1. Clone o repositório

   git clone https://github.com/seu-usuario/api-bancaria-quarkus.git
   cd api-bancaria-quarkus

2. Inicie o banco de dados PostgreSQL
   Na pasta local/, execute o docker-compose para subir o PostgreSQL:
   cd local
   docker-compose up -d
   Aguarde o container ficar em estado healthy antes de prosseguir:
   docker-compose ps

3. Verifique o status do banco
   docker-compose logs postgres

4. Instale as dependências e execute a aplicação
   De volta na raiz do projeto:
   ./mvnw clean install

"Você pode executar sua aplicação no modo de desenvolvimento que habilita o live coding (programação ao vivo) usando:

"./mvnw quarkus:dev"

A aplicação estará disponível em:
http://localhost:8080

🚀 Uso

# Executando a Aplicação
Modo Desenvolvimento (com hot reload)
./mvnw quarkus:dev
Acesse o Dev UI do Quarkus em:
http://localhost:8080/q/dev/
Modo Produção (JAR padrão)
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
Modo Produção (Uber JAR)
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
Build Nativo (GraalVM)
./mvnw package -Dnative
./target/api-bancaria-quarkus-1.0.0-SNAPSHOT-runner
Ou com Docker:
./mvnw package -Dnative -Dquarkus.native.container-build=true
Exemplos de Requisições (Postman / cURL)

1. Autenticação (Login)
   curl -X POST http://localhost:8080/auth/login \
   -H "Content-Type: application/json" \
   -d '{
   "email": "usuario@example.com",
   "senha": "123456"
   }'
2. Cadastrar Cliente (Requer Role GERENTE)
   curl -X POST http://localhost:8080/clientes \
   -H "Content-Type: application/json" \
   -H "Authorization: Bearer TOKEN_JWT" \
   -d '{
   "nome": "João Silva",
   "email": "joao@example.com",
   "senha": "senha123",
   "cpf": "12345678900"
   }'
3. Criar Conta Bancária (Requer Role GERENTE)
   curl -X POST http://localhost:8080/contas \
   -H "Content-Type: application/json" \
   -H "Authorization: Bearer TOKEN_JWT" \
   -d '{
   "clienteId": 1,
   "tipoConta": "CORRENTE"
   }'
4. Buscar Conta por ID
   curl -X GET http://localhost:8080/contas/1 \
   -H "Authorization: Bearer TOKEN_JWT"
5. Realizar Depósito
   curl -X POST http://localhost:8080/contas/1/deposito \
   -H "Content-Type: application/json" \
   -H "Authorization: Bearer TOKEN_JWT" \
   -d '{
   "valor": 500.00,
   "descricao": "Depósito inicial"
   }'
6. Realizar Saque
   curl -X POST http://localhost:8080/contas/1/saque \
   -H "Content-Type: application/json" \
   -H "Authorization: Bearer TOKEN_JWT" \
   -d '{
   "valor": 100.00,
   "descricao": "Saque de emergência"
   }'
7. Realizar Transferência
   curl -X POST http://localhost:8080/contas/1/transferencia \
   -H "Content-Type: application/json" \
   -H "Authorization: Bearer TOKEN_JWT" \
   -d '{
   "valor": 250.00,
   "contaDestino": 2,
   "descricao": "Transferência entre contas"
   }'

⚙️ Configuração
Variáveis de Ambiente
As configurações estão definidas em src/main/resources/application.properties:

# Porta do servidor
quarkus.http.port=8080

# Banco de Dados - PostgreSQL
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=ada.tech
quarkus.datasource.password=turma1660
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5433/bancada

# Hibernate ORM
quarkus.hibernate-orm.log.sql=true

# JWT - Segurança
mp.jwt.verify.publickey.location=publicKey.pem
mp.jwt.verify.issuer=meeting-room-api
smallrye.jwt.sign.key.location=privateKey.pem
Configuração do Banco de Dados
O arquivo local/docker-compose.yml contém a configuração do PostgreSQL:
POSTGRES_DB: bancada
POSTGRES_USER: ada.tech
POSTGRES_PASSWORD: turma1660
POSTGRES_PORT: 5433
Para alterar as credenciais, atualize tanto o docker-compose.yml quanto o application.properties.
Chaves JWT
As chaves pública e privada estão localizadas em:
src/main/resources/privateKey.pem - Assinatura de tokens
src/main/resources/publicKey.pem - Verificação de tokens


# 🛠 Tecnologias Utilizadas
Framework & Linguagem
Quarkus 3.34.2 - Framework Java supersônico e subatômico
Java 25 - Linguagem de programação
Banco de Dados
PostgreSQL 16.4 - Banco de dados relacional
Hibernate ORM Panache - Mapeamento objeto-relacional
Quarkus JDBC PostgreSQL - Driver JDBC
Segurança
JWT (JSON Web Tokens) - Autenticação
SmallRye JWT - Implementação JWT para Quarkus
BCrypt - Hash seguro de senhas
Quarkus ARC - Injeção de dependência
REST & Serialização
Quarkus REST - Implementação Jakarta REST
Jackson - Serialização JSON
Validação
Hibernate Validator - Validação de beans e parâmetros
Jakarta Validation - Validação padrão
Testes
JUnit - Framework de testes unitários
REST Assured - Testes de API REST
Build & Deploy
Maven - Gerenciador de dependências e build
Docker - Containerização

🤝 Contribuição
Agradecemos o interesse em contribuir com este projeto! 
Para garantir qualidade e consistência, siga as orientações abaixo:

## Como Contribuir
Fork o repositório
git clone https://github.com/seu-usuario/api-bancaria-quarkus.git
Crie uma branch para sua feature
git checkout -b feature/sua-feature
Faça as alterações e commit
git add .
git commit -m "feat: descrição clara da sua contribuição"
Push para a branch
git push origin feature/sua-feature
Abra um Pull Request
Descreva as mudanças propostas
Referencie issues relacionadas
Verifique se os testes passam

# Padrão de Commits
Utilize commits semânticos:
feat:Uma nova funcionalidade
fix:Correção de um bug
refactor: Refatoração sem mudança de funcionalidade
style:Formatação, pontuação (não afeta código)
test:Adição ou modificação de testes
docs:Documentação
chore:Tarefas administrativas
Estilo de Código
Siga as convenções JAVA padrão
Use nomes descritivos para classes, métodos e variáveis
Mantenha métodos pequenos e focados em uma responsabilidade
Adicione comentários em lógicas complexas
Realize testes locais antes de submeter 
Reportar Issues
Verifique se o issue já existe
Descreva o problema em detalhes
Forneça exemplos de código/passos para reproduzir
Inclua informações do ambiente (SO, versão Java, etc.)

📄 Licença
Este projeto está licenciado sob a MIT License - veja o arquivo
LICENSE para detalhes.
A licença MIT permite:
✅ Uso comercial
✅ Modificação
✅ Distribuição
✅ Uso privado
Com a única exigência de:
📌 Incluir a licença e aviso de copyright

👩‍💻 Autores / Créditos
Desenvolvido por:
Estela Bettarello
Agradecimentos especiais aos instrutores e colegas da ADA Tech pelo apoio e aprendizado.

📊 Status do Projeto
<img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento%20Ativo-brightgreen" alt="Status"></img> <img src="https://img.shields.io/badge/License-MIT-blue" alt="License"></img> <img src="https://img.shields.io/badge/Java-25-orange" alt="Java"></img> <img src="https://img.shields.io/badge/Quarkus-3.34.2-red" alt="Quarkus"></img>

## ✅ Funcionalidades Implementadas
Autenticação com JWT
Gerenciamento de Clientes
Gerenciamento de Contas Bancárias
Depósitos
Saques
Transferências entre contas
Validação de entrada
Segurança com roles (GERENTE, CLIENTE)
#🚧 Funcionalidades em Desenvolvimento
Extrato bancário
Agendamento de transferências
Relatórios financeiros
Integração com gateway de pagamento

🎯 Roadmap
Melhorias de performance e otimização de queries
Testes de integração mais robustos
Documentação Swagger/OpenAPI
Suporte a múltiplas moedas
Sistema de limites de crédito

📞 Suporte
Para dúvidas, sugestões ou relatos de bugs, abra uma Issue no repositório GitHub.

Desenvolvido com ❤️ para a comunidade de desenvolvedores Java.

