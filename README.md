---

# 📋 Aplicativo de Tarefas

Esse é um aplicativo Android simples para gerenciamento de tarefas pessoais, desenvolvido como parte de um projeto acadêmico da disciplina de Mobile do 5º semestre do curso de Bacharelado em Engenharia de Software, ano de 2025.

## ✅ Funcionalidades Implementadas

- ✅ RF01: Adicionar novas tarefas.
- ✅ RF02: Listar todas as tarefas cadastradas.
- ✅ RF03: Editar e excluir tarefas existentes.
- ✅ RF04: Navegação entre telas com Intents explícitas.
- ✅ RF05: Menu de opções para acessar funcionalidades.
- ✅ RF06: Menu de contexto acionado por clique longo nos itens da lista.

## 🚀 Como Executar o Projeto

1. Clone este repositório:
```
bash
git clone https://github.com/FelipeMarchiC/PersonalTasks.git
```

2. Abra o projeto no **Android Studio**.
3. Conecte um dispositivo físico ou utilize um emulador.
4. Execute o app com o botão "Run".

> Nenhuma permissão especial é necessária para rodar o app.

## 🦗 Instruções
1. Ao abrir o aplicativo, ele irá exibir para você sua lista de tarefas. Na primeira vez que abrir é normal não haver nenhuma tarefa criada de antemão.
2. Com esse aplicativo, é possível criar tarefas com o "+" no canto superior direito. Você então será enviado para um formulário onde vai preencher as informações para criar sua tarefa.
3. Ao dar um clique longo sobre as tarefas, você abre um menu de opções que lhe permite editá-las, apagá-las ou ver seus detalhes, clicar em editar ou ver detalhes te leva ao formulário, apagar lhe envia um toast.

## 🎥 Demonstração em Vídeo

Veja uma execução completa (em 1 minuto) demonstrando os requisitos funcionais:

📺 [Link para o vídeo demonstrativo (Entrega 1)](https://youtu.be/tliw9RQ4bWY)
📺 [Link para o vídeo demonstrativo (Entrega 2)](https://youtu.be/Dl2b1LnJzls)

## 📁 Estrutura Principal do Projeto
```
app/
├── manifests/
│   └── AndroidManifest.xml
├── kotlin+java/
│   └── bes.mobile.personaltasks/
│       ├── adapter/
│       ├── controller/
│       ├── model/
│       └── view/
├── res
│   ├── drawable/
│   ├── layout/
│   ├── menu/
│   └── values/
```
