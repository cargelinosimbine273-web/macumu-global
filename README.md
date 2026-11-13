# Macumu Global — Android

Aplicação Android de marketplace e agregação de ofertas, em desenvolvimento.

## Estado desta entrega

Esta é uma **versão de testes (debug)** preparada para compilação no GitHub Actions. Não deve ser tratada como versão de produção nem utilizada para transacções financeiras reais.

### O que está incluído
- Interface Android com Jetpack Compose.
- Ecrãs de comprador, vendedor, afiliado e gestão.
- Persistência local com Room.
- Dependências Firebase Authentication e Firestore incluídas no projecto.
- Workflow GitHub Actions para gerar e guardar o APK Debug como artefacto.

### Gerar o APK no GitHub
1. Crie um repositório GitHub vazio.
2. Extraia o ZIP e carregue **o conteúdo da pasta** para a raiz do repositório (não carregue a pasta ZIP como subpasta).
3. Faça `Commit changes` para a branch `main`.
4. Abra o separador **Actions**.
5. Entre no workflow **Android APK** e aguarde a conclusão.
6. Abra a execução concluída e descarregue `macumu-global-debug-apk` na secção **Artifacts**.
7. Extraia o artefacto ZIP para encontrar `app-debug.apk`.

Também pode iniciar a compilação manualmente em **Actions → Android APK → Run workflow**.

### Requisitos de compilação
- JDK 17
- Android Gradle Plugin 9.1.1
- Gradle 9.3.1
- Android SDK compatível com `compileSdk 36 (extension 1)`

> Nota técnica: o ZIP original não continha `gradlew`, `gradlew.bat` nem `gradle-wrapper.jar`. Por isso, o workflow instala Gradle 9.3.1 directamente e não depende do Gradle Wrapper. Para compilar localmente via terminal, instale Gradle 9.3.1; a integração do wrapper será feita numa revisão posterior.

## Segurança e limitações conhecidas
- O acesso administrativo por PIN fixo foi desactivado nesta versão. Um PIN embutido no APK pode ser extraído. O painel só deve ser reactivado com autenticação administrativa no servidor e regras de segurança.
- Os dados de negócio ainda dependem substancialmente da base local Room; a sincronização central multiutilizador precisa de ser implementada e testada.
- A presença das bibliotecas Firebase não significa que todos os fluxos estejam integrados ou protegidos.
- Não foi identificada integração de produção com M-Pesa/e-Mola nesta auditoria.
- Não utilizar carteira, escrow, comissões ou levantamentos como dinheiro real até existir backend transaccional, validação do fornecedor de pagamento, reconciliação e auditoria.
- O workflow gera APK Debug, assinado com chave de depuração. Para distribuição pública é necessária configuração de assinatura Release e gestão segura da chave.

## Firebase
O ficheiro `app/google-services.json` é necessário para a configuração Firebase actual. Confirme no Firebase Console que as APIs e as restrições da chave estão correctamente configuradas. Nunca coloque credenciais de servidor, service-account JSON, chaves privadas ou ficheiros de assinatura no repositório.

## Próxima etapa
Depois de obter o APK Debug, instalar e testar em Android real: navegação, registo/login, persistência, carrinho, checkout simulado, vendedor e afiliado. Em seguida, corrigir os problemas encontrados e implementar backend seguro antes da versão Release.
