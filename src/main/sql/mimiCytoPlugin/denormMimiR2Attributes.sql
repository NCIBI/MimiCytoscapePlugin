-- =============================================
--	Jing Gao, David States
-- =============================================
USE mimiR2v1
IF OBJECT_ID ( 'mimiCytoPlugin.denormMimiR2Attributes', 'P' ) IS NOT NULL 
    DROP PROCEDURE mimiCytoPlugin.denormMimiR2Attributes
GO
CREATE PROCEDURE [mimiCytoPlugin].[denormMimiR2Attributes]	
	@type int,	
	@geneIDs varchar (max),
	@interactionIDs varchar (max)
AS

Declare @GeneID table (
	geneID int
)

Declare @InteractionID table(
	ggIntID int
)

If (@type<20)
Begin
	Insert Into @GeneID (geneID)
	Select token
	From dbo.SplitDelim(@geneIDs, ' ')
End

If (@type>20)
Begin
	Insert Into @InteractionID (ggIntID)
	Select token
	From dbo.SplitDelim(@interactionIDs, ' ')
End


--****************get GENE related info
--get gene basic attributes (1:1 mapping)
if (@type=1)
Begin
	Select sg.geneid, dga.attrType, dga.attrValue
	From @GeneID sg
		join denorm.GeneAttribute dga on sg.geneid =dga.geneid
	Where attrType='chromosome' or  attrType='description'  
		or attrType='locustag' or attrType='map_loc' 
		or attrType='organism' or attrType='taxid' 
		or attrType='kegg gene' or attrType='gene type' 
	Order by sg.geneid, dga.attrType
	
End

--get gene go term, does not use denorm tables since no go_id in GeneAttributes
if (@type=2)
Begin
	Select distinct sg.geneid, dga.attrValue, dga.attrType
	From @GeneID sg join denorm.GeneAttribute dga on sg.geneid=dga.geneid
		and dga.attrType in ('Component', 'Function','Process')
	Order by sg.geneid, dga.attrType
End

--get gene name (mutiple rst)
if (@type=3)
Begin
	Select distinct sg.geneid, dga.attrValue
	From @GeneID sg 
		join denorm.GeneAttribute dga on sg.geneid =dga.geneid
	Where dga.attrType='synonyms'
	order by sg.geneid, dga.attrValue
End

--get gene pathway info (multiple rst) does not use denorm since wrong mapping for pathName->description
if (@type=4)
Begin
	Select distinct sg.geneid , dga.attrType, dga.attrValue
	From @GeneID sg
		join denorm.GeneAttribute dga on sg.geneid = dga.geneid 
			and dga.attrType = 'pathway' and dga.attrValue is not null
		order by sg.geneid, dga.attrType, dga.attrValue
End



--get complexname (multiple rst) not exist in GeneAttribute



--******************get INTERACTION related info using interactionIDs
--InteractionType Provenance PubMed Function  Process component

--get interaction type Provenance PubMed
if (@type=21)
Begin
	Select distinct sg.ggIntID, dggia.attrType, dggia.attrValue
	From @InteractionID sg 
		join denorm.GeneGeneInteractionAttribute dggia on  sg.ggIntID = dggia.ggIntID
			and dggia.attrType in ('InteractionType','Provenance','PubMed','function','component','process') 
	Order by sg.ggIntID, dggia.attrType

End

--get interaction pathway table empty


--get interaction go term 
if (@type =22)
Begin	
	Select distinct sg.ggIntID, dggia.attrType, dggia.attrValue 
    From @InteractionID sg
		join denorm.GeneGeneInteractionAttribute dggia on sg.ggIntID=dggia.ggIntID
	Where dggia.attrType in ('function','component','process')
	Order by sg.ggIntID, dggia.attrType
End
GO
GRANT EXECUTE, VIEW DEFINITION ON mimiCytoPlugin.denormMimiR2Attributes
    TO db_executor;
GO