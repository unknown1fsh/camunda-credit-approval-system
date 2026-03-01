# BPMN Process Files

## Not

Bu dizindeki BPMN dosyaları basitleştirilmiş versiyonlardır.
Production kullanımı için Camunda Modeler kullanarak görsel olarak tasarlanmalı ve aşağıdaki özellikleri içermelidir:

### credit-approval-main.bpmn ✅ (Temel yapı mevcut)
- Event-based gateway
- Parallel gateway (fork/join)
- Multi-instance user tasks
- Timer boundary events
- Compensation events
- Error boundary events
- Call activities

### Eksik BPMN Dosyaları (Camunda Modeler ile oluşturulmalı):
- risk-assessment-subprocess.bpmn
- document-collection-subprocess.bpmn
- parallel-evaluation-subprocess.bpmn
- compensation-process.bpmn

Bu dosyalar için plan dokümantasyonundaki detaylı akış diyagramlarını kullanarak
Camunda Modeler'de görsel olarak oluşturun.
