package br.com.alura.logs.controller;

import br.com.alura.logs.dto.CursoDto;
import br.com.alura.logs.exceptions.InternalErrorException;
import br.com.alura.logs.model.CursoModel;
import br.com.alura.logs.service.CursoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins="*", maxAge=3600)
@RequestMapping("/cursos")
public class CursoController {

    private static Logger logger = LoggerFactory.getLogger(CursoController.class);
	
	final CursoService cursoService;
	
	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}
	
	@PostMapping
	 public ResponseEntity<Object> saveCurso(@RequestBody @Valid CursoDto cursoDto){
        try {
            logger.info("Iniciando processo de insercao de registro de novo curso");

            logger.info("Chamando o cursoService para validar se numero de matricula ja existe");

            if(cursoService.existsByNumeroMatricula(cursoDto.getNumeroMatricula())) {
                logger.warn("Novo registro nao inserido, o numero de matricula ja existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("O número de matricula do curso já esta em uso!");
            }

            logger.info("Validacao de numero de matricula concluida");
            logger.info("Chamando o cursoService para validar se numero de curso ja existe");

            if(cursoService.existsByNumeroCurso(cursoDto.getNumeroCurso())) {
                logger.warn("Novo registro nao inserido, o numero do curso ja existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("O número do curso já esta em uso!");
            }

            logger.info("Validacao de numero de curso concluida");
            logger.info("Validacoes de cursoService sobre cursoDTO executadas com sucesso");
            logger.info("Chamando cursoService.save para armazenar o novo registro");

            var cursoModel = new CursoModel();
            BeanUtils.copyProperties(cursoDto, cursoModel);
            cursoModel.setDataInscricao(LocalDateTime.now(ZoneId.of("UTC")));

            logger.info("Novo registro de curso salvo com sucesso");

            return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.save(cursoModel));
        } catch (CannotCreateTransactionException | DataAccessException e) {
            logger.error("Erro de comunicacao com banco de dados", e);
            throw new InternalErrorException("Erro momentaneo. Por favor, tente novamente mais tarde");
        }
	}
	
	
	@GetMapping
	public ResponseEntity<Page<CursoModel>> getAllCursos(@PageableDefault(page = 0, size = 10, sort = "dataInscricao", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            logger.info("Chamando cursoService.findAll para buscar todos os registros");
            return ResponseEntity.status(HttpStatus.OK).body(cursoService.findAll(pageable));
        } catch (CannotCreateTransactionException | DataAccessException e) {
            logger.error("Erro de comunicacao com banco de dados", e);
            throw new InternalErrorException("Erro momentaneo. Por favor, tente novamente mais tarde");
        }
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getOneCursos(@PathVariable(value="id") UUID id) {
        try {
            logger.info("Chamando cursoService.findById para buscar um registro por UUID");
            Optional<CursoModel> cursoModelOptional = cursoService.findById(id);
            logger.info("Validando por cursoModelOptional se o UUID existe");
            if (cursoModelOptional.isEmpty()) {
                logger.warn("Validacao em cursoModelOptional retornou vazio, UUID nao encontrado!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
            }
            logger.info("O registro foi encontrado por cursoService no banco de dados!");
            return ResponseEntity.status(HttpStatus.OK).body(cursoModelOptional.get());
        } catch (CannotCreateTransactionException | DataAccessException e) {
            logger.error("Erro de comunicacao com banco de dados", e);
            throw new InternalErrorException("Erro momentaneo. Por favor, tente novamente mais tarde");
        }

}
	
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCursos(@PathVariable(value = "id") UUID id){
        try {
            logger.info("Chamando cursoService.findById para excluir um registro por UUID");
            Optional<CursoModel> cursoModelOptional = cursoService.findById(id);
            logger.info("Validando por cursoModelOptional se o UUID existe");
            if (!cursoModelOptional.isPresent()) {
                logger.warn("Tentativa de exclusao abortada! UUID informado nao encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
            }
            cursoService.delete(cursoModelOptional.get());
            logger.info("O registro foi excluido por cursoService do banco de dados!");
            return ResponseEntity.status(HttpStatus.OK).body("Curso excluído com sucesso!");
        } catch (CannotCreateTransactionException | DataAccessException e) {
            logger.error("Erro de comunicacao com banco de dados", e);
            throw new InternalErrorException("Erro momentaneo. Por favor, tente novamente mais tarde");
        }

    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCursos(@PathVariable(value = "id") UUID id, @RequestBody @Valid CursoDto cursoDto) {
        try {
            logger.info("Chamando cursoService.findById para alterar um registro por UUID");
            Optional<CursoModel> cursoModelOptional = cursoService.findById(id);
            logger.info("Validando por cursoModelOptional se o UUID existe");
            if (!cursoModelOptional.isPresent()) {
                logger.warn("Validacao em cursoModelOptional retornou vazio, UUID nao encontrado!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado!");
            }
            var cursoModel = new CursoModel();
            BeanUtils.copyProperties(cursoDto, cursoModel);
            cursoModel.setId(cursoModelOptional.get().getId());
            cursoModel.setDataInscricao(cursoModelOptional.get().getDataInscricao());

            CursoModel cursoModelAtualizado = cursoService.save(cursoModel);
            logger.info("O registro foi alterado por cursoService do banco de dados!");
            return ResponseEntity.status(HttpStatus.OK).body(cursoModelAtualizado);
        } catch (CannotCreateTransactionException | DataAccessException e) {
            logger.error("Erro de comunicacao com banco de dados", e);
            throw new InternalErrorException("Erro momentaneo. Por favor, tente novamente mais tarde");
        }

    }

}
