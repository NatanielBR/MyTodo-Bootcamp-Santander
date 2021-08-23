# MyTodo-Bootcamp-Santander

# Descrição

MyTodo é um aplicativo que permite o usuario fazer uma lista de atividades, definir data e hora, e o aplicativo
irá produzir uma notificação (com som de alarme) quando chegar na data marcada.

# Recursos utilizados

## Banco de dados

Foi utilizado o ObjectBox como banco de dados. O motivo de não utilizar o Room ou qualquer outro que utilize o
SQL é por conta da experiencia utilizando em um outro aplicativo, o Scamanga. No começo utilizei o Room para
armazenar as entidades no SQLITE mas enquanto o aplicativo se tornava mais complexo foi nescessario refazer
as tabelas e aí encontrei o problema. Buscando soluções cheguei no ObjectBox e o resultado foi bem satisfatório.

O ObjectBox armazena os dados em objetos e tudo é feito através de objetos, algo que em Java/Kotlin ajuda bastante
por conta da natureza da linguagem. Por conta disso, o ObjectBox será minha primeira escolha até que encontre
problemas ou a regra de negócio exija um outro formato.

## UI

No Android existe o Compose e o XML (não conheço um nome para isso), entretando por certas razões escolhi o XML.
Através do meu outro aplicativo, ganhei mais experiencia no XML. Alem disso, não tenho muita experiencia no
Compose e caso eu adote ele, teria que ser em outro momento.

## Qualquer biblioteca extra

Este é um projeto simples e por conta disso não precisa de acesso a internet ou algo assim. Entretando existe
bibliotecas que facilida o desenvolvimento ou deixa o codigo mais seguro, estavel e eficiente. Preferi não utilizar
tais bibliotecas para não deixar o codigo muito complexo e não aumentar o tamanho do aplicativo de forma desnecessaria.
Alguns momentos eu escrevi classes e metodos utilitarios para ajudar o desenvolvimento e tambem para evitar importar
uma biblioteca gigantesca somente para ter acesso a um metodo.

# Recursos desativados

Desativei o modo noturno por conta de um bug que fazia o fragment sumir, tentei de varías formas mas não consegui
resolver. Como descobri esse bug no final do desenvolvimento e se eu removesse o Navigation para uma forma mais
direta de fazer a troca de fragments eu iria mexer bastante no codigo e talvez iria criar mais bugs, resolvi
desabilitar-lo.

Existe uma classe que foi utilizada durante o desenvolvimento mas durante a produção não será utilizada, porem eu
decidi mante-la por que ela é importante para testar o aplicativo.

# Lint

Após o desenvolvimento, executei o Lint e fiz quase todas as recomendações apontandas. A unica que não
fiz foi o "The launcher icon shape should use a distinct silhouette", onde pelo meu julgamento isso não impactava
diretamente o aplicativo.
